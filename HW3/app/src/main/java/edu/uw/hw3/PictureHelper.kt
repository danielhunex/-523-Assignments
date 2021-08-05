package edu.uw.hw3

import android.graphics.*
import android.widget.ImageView
import java.io.File

class PictureHelper {

    /***
     * Loads image from file using the path provided
     */
    public fun getImage(path: String?): Bitmap? {
        if (path != null && path != "") {
            val imgFile = File(path)
            var length = imgFile.length();
            if (imgFile.exists()) {
                val bmOptions = BitmapFactory.Options()
                var bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions)

                var bmpWidth = bitmap.getWidth()
                var bmpHeight = bitmap.getHeight()

                val matrix = Matrix()
                matrix.postScale(1.0F, 1F)
                matrix.postRotate(90F)  //Note to Professor: in my phone the image was taken portrait and displayed landscape, so i rotated it. It might behave differently in your phone

                val resizedBitmap: Bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true)
                return resizedBitmap;
            }
        }
        return null;
    }

    /***
     * adds text to the bitmap provided
     */
    public fun addTextToBitmap(src: Bitmap, text: String, size: Float): Bitmap? {
        var bitmapConfig = src.config

        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }

        val editedPicture = src.copy(bitmapConfig, true);
        val canvas = Canvas(editedPicture)

        var tf = Typeface.create("Helvetica", Typeface.BOLD);
        var paint = Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(191, 64, 191));
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(size);

        var bounds = Rect();
        paint.getTextBounds(text, 0, text.length, bounds);
        var x = (canvas.getWidth() - bounds.width()) / 2;
        var y = (canvas.getHeight() + bounds.height()) / 2

        canvas.drawBitmap(editedPicture, 0F, 0F, null)
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
        return editedPicture
    }

    public fun resize(bitmap: Bitmap?): Bitmap? {
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