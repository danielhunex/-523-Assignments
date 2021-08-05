package edu.uw.hw3

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import edu.uw.hw3.databinding.FragmentHomeBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {
    private var PHOTO_FILE_TYPE: String = ".jpg"
    private val REQUEST_IMAGE_CAPTURE = 1

    private var _photoFile: File? = null
    private lateinit var _firstPhotoPath: String;
    private var _binding: FragmentHomeBinding? = null
    private var isFirstPictureTaken: Boolean = false
    private lateinit var homeViewModel: HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonEdit.setOnClickListener {
            val imageToEditPath = _firstPhotoPath +""
            val action = HomeFragmentDirections.actionHomeToEdit(imageToEditPath)
            findNavController().navigate(action)
        }

        ensureCameraPermission()

        //face detection and swapping
        binding.buttonSwap.setOnClickListener {
            if (homeViewModel.firstBitmap.value != null) {
                var detectorHelper = FaceDetectorHelper()
                var firstBitmap: Bitmap? = homeViewModel.originalFirstBitmap.value
                var secondBitmap: Bitmap? = homeViewModel.originalSecondBitmap.value

                var facesFromFirstPhotoTask =
                    firstBitmap?.let { it1 -> detectorHelper.detectFaces(it1) }

                var facesFromSecondPhotoTask =
                    secondBitmap?.let { it1 -> detectorHelper.detectFaces(it1) }

                var face1: com.google.mlkit.vision.face.Face? = null
                var face2: com.google.mlkit.vision.face.Face? = null

                facesFromFirstPhotoTask?.addOnSuccessListener { faces ->
                    face1 = faces[0]
                }?.addOnFailureListener {
                    Log.d("Face Detaction", "Failed to detect face of the first photo")
                }
                facesFromSecondPhotoTask?.addOnSuccessListener { faces ->
                    face2 = faces[0]

                    swapFaces(
                        face1,
                        face2,
                        homeViewModel.originalFirstBitmap.value!!,
                        homeViewModel.secondBitmap.value!!,
                        { b: Bitmap -> homeViewModel.setSecondBitmap(b) }
                    )
                    swapFaces(
                        face2,
                        face1,
                        homeViewModel.originalSecondBitmap.value!!,
                        homeViewModel.firstBitmap.value!!,
                        { b: Bitmap -> homeViewModel.setFirstBitmap(b) }
                    )

                }?.addOnFailureListener {
                    Log.d(
                        "Face Detaction",
                        "Failed to detect face of the second photo or swapping failed"
                    )
                }
            }
        }


        //take photo
        binding.buttonCapture.setOnClickListener {
            startCameraIntent()
        }

        // To drawview view

        binding.buttonDrawView.setOnClickListener{
            val intent = Intent(activity, DrawviewSimulation::class.java)
            intent.putExtra("pathOfPictureToEdit", _firstPhotoPath +"")
            startActivity(intent)
        }
        // back/clear to the original photo
        binding.buttonClear.setOnClickListener()
        {
            homeViewModel.setFirstBitmap(
                homeViewModel.originalFirstBitmap.value!!.copy(
                    homeViewModel.originalFirstBitmap.value!!.config,
                    true
                )
            )
            homeViewModel.setSecondBitmap(
                homeViewModel.originalSecondBitmap.value!!.copy(
                    homeViewModel.originalSecondBitmap.value!!.config,
                    true
                )
            )
        }

        //setup observer for photo changes
        homeViewModel.firstBitmap.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                binding.imageviewFirst.setImageBitmap(
                    bitmap
                )
            }
        }

        homeViewModel.secondBitmap.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                binding.imageviewSecond.setImageBitmap(
                    bitmap
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun swapFaces(
        face1: com.google.mlkit.vision.face.Face?,
        face2: com.google.mlkit.vision.face.Face?,
        fromPhoto: Bitmap,
        toPhoto: Bitmap,
        swap: (b: Bitmap) -> Unit
    ) {

        if (face1 == null || face2 == null || fromPhoto == null || toPhoto == null) {
            Toast.makeText(activity, "Unable to swap faces", Toast.LENGTH_LONG).show()
            return
        }
        val pixels =
            IntArray((face1?.boundingBox?.height() ?: 0) * (face1?.boundingBox?.width() ?: 0))

        //get the face boundary from photo 1
        face1?.boundingBox?.let {
            fromPhoto.getPixels(
                pixels,
                0,
                it.width(),
                it.left,
                it.top,
                it.width(),
                it.width()
            )
        }
        try {

            //set photo 2 pixels from face 1
            face1?.boundingBox?.let {
                face2?.boundingBox?.let { sec ->
                    toPhoto.setPixels(
                        pixels,
                        0,
                        Math.min(it.width(), toPhoto.width),
                        sec.left,
                        sec.top,
                        Math.min(it.width(), toPhoto.width),
                        Math.min(it.height(), toPhoto.height)
                    )
                }
            }

            swap(toPhoto)
        } catch (ex: java.lang.Exception) {
            Log.d("Error", ex.message + "")
        }
    }



    /***
     * Makes sure that the app has Camera aand storage permission
     */
    private fun ensureCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 0
                )
            }
        } else {
            if (activity?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        android.Manifest.permission.CAMERA
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 0
                    )
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFilePath(): File {
        //take the year month day hour minutes and seconds of today to create unique string
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) // trying to get unique name
        // construct picture filepath to be taken
        val imageFileName = "photo_$timeStamp$PHOTO_FILE_TYPE"
        //create specific folder in the pictures directory for the Hw2 app
        val mediaStorageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (mediaStorageDir != null && !mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Toast.makeText(activity, "unable to create directory for pictures", Toast.LENGTH_SHORT);
        }
        Log.d("Photo Directory", mediaStorageDir?.absolutePath + "")
        //create file for the picture
        var image = File(mediaStorageDir?.absolutePath + "/" + imageFileName)
        //take the absolute path of the current photo to be taken
        return image
    }

    //takes pictures and saves it in the specific folder for the app as created by createImageFilePath function
    private fun startCameraIntent() {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            _photoFile = createImageFilePath()

            if (_photoFile != null) {
                val photoURI = activity?.let {
                    FileProvider.getUriForFile(
                        it,
                        "edu.uw.hw3.fileprovider",
                        _photoFile!!
                    )
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } catch (ex: Exception) {

            Toast.makeText(activity, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            previewPicture(_photoFile!!.absolutePath)
        } else {
            Toast.makeText(
                activity,
                "Request cancelled or something went wrong.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun previewPicture(path: String?) {

        var helper = PictureHelper()
        var bitmap = helper.getImage(path)
        if (bitmap != null) {

            if (!isFirstPictureTaken) {
                _firstPhotoPath = path!!
                isFirstPictureTaken = true
                resize(bitmap)?.let { homeViewModel.setFirstBitmap(it) }

                bitmap = helper.getImage(path);
                resize(bitmap)?.let { homeViewModel.setOriginalFirstBitmap(it) }
                // binding.imageviewFirst.setImageBitmap(resize(bitmap))
            } else {

                resize(bitmap)?.let { homeViewModel.setSecondBitmap(it) }

                bitmap = helper.getImage(path)
                resize(bitmap)?.let { homeViewModel.setOriginalSecondBitmap(it) }
            }

        }
    }

    fun resize(bitmap: Bitmap?): Bitmap? {
        if (bitmap != null) {
            var bmpWidth = bitmap.getWidth()
            var bmpHeight = bitmap.getHeight()

            val matrix = Matrix()
            matrix.postScale(0.35F, 0.35F)
            //matrix.postRotate(90F)  //Note to Professor: in my phone the image was taken portrait and displayed landscape, so i rotated it. It might behave differently in your phone

            val resizedBitmap: Bitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true)
            return resizedBitmap;

        }
        return null;
    }
}