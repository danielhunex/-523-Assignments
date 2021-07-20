package edu.uw.hm2

import android.graphics.*
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class PictureEditor : AppCompatActivity() {
    private lateinit var _imageEditor: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_editor)

        _imageEditor = findViewById(R.id.imv_editor)

        val imagePath = intent.getStringExtra("pathOfPictureToEdit")

        editAndPreview(imagePath)

    }

    private fun editAndPreview(path: String?) {
        var helper = PictureHelper()
        var bitmap = helper.getImage(path);

        if (bitmap != null) {
            var editedImage = helper.addTextToBitmap(bitmap, "UW EE 523", 300F)
            _imageEditor.setImageBitmap(editedImage)
        }
    }
}