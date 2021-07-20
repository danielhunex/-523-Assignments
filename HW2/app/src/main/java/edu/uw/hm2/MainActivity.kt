package edu.uw.hm2

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val PHOTO_FILE_TYPE: String = ".jpg"
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var _currentPhotoPath: String
    private var _photoFile: File? = null
    private lateinit var _photosTaken: Array<String?>
    private var _currentPhotoIndex: Int = 0;
    private var _totalPhotoesInMediaDir: Int = 0;

    private lateinit var _btnCapture: Button
    private lateinit var _btnNext: Button
    private lateinit var _btnPrevious: Button
    private lateinit var _btnEdit: Button
    private lateinit var _imageViewer: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ensureCameraPermission();

        _btnCapture = findViewById(R.id.btn_capture);
        _btnNext = findViewById(R.id.btn_next)
        _btnPrevious = findViewById(R.id.btn_previous)
        _btnEdit = findViewById(R.id.btn_edit)
        _imageViewer = findViewById(R.id.imv_preview)

        //opens camera intent
        _btnCapture.setOnClickListener {
            startCameraIntent()
        }

        _btnNext.setOnClickListener {
            loadNextPicture()
        }
        _btnPrevious.setOnClickListener {
            loadPreviousPicture()
        }
        _btnEdit.setOnClickListener() {
            val intent = Intent(this, PictureEditor::class.java)
            intent.putExtra("pathOfPictureToEdit", _photosTaken[_currentPhotoIndex])
            startActivity(intent)

        }

        getAllPicturesTaken();
        enableNavButton()
    }

    /***
     * navigates to the next picture to display from pictures takep by this app
     */
    private fun loadNextPicture() {
        if (_totalPhotoesInMediaDir > 0 && _currentPhotoIndex < _totalPhotoesInMediaDir - 1) {
            _currentPhotoIndex++;
            var currentPhotoPath = _photosTaken[_currentPhotoIndex];
            previewPicture(currentPhotoPath)
            enableNavButton()
        }
    }

    /***
     *navigates to the previous picture to display from the pictures taken by this app
     */
    private fun loadPreviousPicture() {
        if (_totalPhotoesInMediaDir > 0 && _currentPhotoIndex > 0) {
            _currentPhotoIndex--;
            var currentPhotoPath = _photosTaken[_currentPhotoIndex];
            previewPicture(currentPhotoPath)
            enableNavButton()
        }
    }

    /**
     * enables /disables navigation (btnNext, btnPrevious) buttons
     */
    private fun enableNavButton() {
        _btnNext.isEnabled =
            _totalPhotoesInMediaDir > 0 && _currentPhotoIndex < _totalPhotoesInMediaDir - 1
        _btnPrevious.isEnabled = _totalPhotoesInMediaDir > 0 && _currentPhotoIndex > 0
        _btnEdit.isEnabled = _totalPhotoesInMediaDir > 0
    }

    /***
     * gets the list of photo files taken by hw2 Little Camera app
     * and stores them in _photoesTaken folder
     */
    private fun getAllPicturesTaken() {
        try {
            val mediaStorageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val files = File(mediaStorageDir?.absolutePath).listFiles();
            if (files != null && files.size > 0) {
                _totalPhotoesInMediaDir = files.size
                _currentPhotoIndex = 0
                _photosTaken = arrayOfNulls<String>(files.size)
                files?.mapIndexed { index, item ->
                    _photosTaken[index] = item?.absolutePath
                }
                previewPicture(_photosTaken[_currentPhotoIndex])
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT)
        }
    }

    /***
     * Makes sure that the app has Camera aand storage permission
     */
    private fun ensureCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                this.requestPermissions(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
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
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 0
                )
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFilePath(): File {
        //take the year month day hour minutes and seconds of today to create unique string
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) // trying to get unique name
        // construct picture filepath to be taken
        val imageFileName = "photo_" + timeStamp + PHOTO_FILE_TYPE
        //create specific folder in the pictures directory for the Hw2 app
        val mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (mediaStorageDir != null && !mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Toast.makeText(this, "unable to create directory for pictures", Toast.LENGTH_SHORT);
        }
        Log.d("Photo Directory", mediaStorageDir?.absolutePath + "")
        //create file for the picture
        var image = File(mediaStorageDir?.absolutePath + "/" + imageFileName)
        //take the absolute path of the current photo to be taken
        _currentPhotoPath = image.absolutePath
        return image
    }

    //takes pictures and saves it in the specific folder for the app as created by createImageFilePath function
    private fun startCameraIntent() {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            _photoFile = createImageFilePath()

            if (_photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "edu.uw.hm2.fileprovider",
                    _photoFile!!
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } catch (ex: Exception) {

            Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            previewPicture(_photoFile!!.absolutePath)
            getAllPicturesTaken()
            _currentPhotoIndex = _totalPhotoesInMediaDir - 2;
            loadNextPicture()

        } else {
            Toast.makeText(this, "Request cancelled or something went wrong.", Toast.LENGTH_SHORT);
        }
    }

    private fun previewPicture(path: String?) {

        var helper = PictureHelper()
        var bitmap = helper.getImage(path);
        if (bitmap != null) {
            _imageViewer.setImageBitmap(bitmap)
        }
    }
}
