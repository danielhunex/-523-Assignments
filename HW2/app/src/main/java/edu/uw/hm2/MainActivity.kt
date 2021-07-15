package edu.uw.hm2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var _btnCapture: Button;
    private lateinit var _currentPhotoPath: String
    private var photoFile: File? = null
    val CAPTURE_IMAGE_REQUEST = 1
    private lateinit var _imageViewer: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ensureCameraPermission();
        _btnCapture = findViewById(R.id.btn_capture);
        _imageViewer = findViewById(R.id.imv_preview)
        _btnCapture.setOnClickListener {

            capturePhoto();
        }


    }

    private fun ensureCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                this.requestPermissions(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 0
                )
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) != PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 0
                )
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {

        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) // trying to get unique name
        val imageFileName = "photo_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        _currentPhotoPath = image.absolutePath
        return image
    }

    private fun capturePhoto() {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = createImageFile()
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "edu.uw.hm2.fileprovider",
                    photoFile!!
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
            }
        } catch (ex: Exception) {

            val makeText = Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            _imageViewer.setImageBitmap(myBitmap)
        } else {
            Toast.makeText(this, "Request cancelled or something went wrong.",Toast.LENGTH_SHORT);
        }
    }


}