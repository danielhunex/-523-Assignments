package edu.uw.hw3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import jp.wasabeef.blurry.Blurry

class FaceDetectorHelper {

    fun detectFaces(bitmap: Bitmap): Task<MutableList<Face>> {

        val image = InputImage.fromBitmap(bitmap, 0)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        val detector = FaceDetection.getClient(options)



        return detector.process(image)

    }

    fun bitmapBlur(bitmap: Bitmap, context:Context, imageView:ImageView) {

        Blurry.with(context)
            .radius(10)
            .sampling(8)
            .color(Color.argb(66, 255, 255, 0))
            .async()
            .animate(500)
            .from(bitmap)
            .into(imageView)
    }

}