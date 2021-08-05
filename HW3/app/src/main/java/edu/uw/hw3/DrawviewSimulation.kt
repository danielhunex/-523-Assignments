package edu.uw.hw3

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import com.divyanshu.draw.widget.DrawView

class DrawviewSimulation : AppCompatActivity() {

    private var _drawView: DrawView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawview_simulation)


        _drawView = findViewById(R.id.draw_view)
        _drawView?.setStrokeWidth(100.0f)
        _drawView?.setColor(Color.WHITE)

        val imagePath = intent.getStringExtra("pathOfPictureToEdit")

        // Will run after every stroke drew
        _drawView?.setOnTouchListener { _, event ->
            // As we have interrupted DrawView's touch event,
            // we first need to pass touch events through to the instance for the drawing to show up mydrawView?.onTouchEvent(event)
            // Then if user finished a touch event, run an action

            if (event.action == MotionEvent.ACTION_UP) {
                //action goes here
                  Toast.makeText(this,"Drawview is cool", Toast.LENGTH_SHORT).show()

            }
            true
        }
        showBackground(imagePath)
    }

    fun showBackground(path:String?)
    {
        var helper = PictureHelper()

        _drawView?.background= BitmapDrawable(getResources(), helper.resize(helper.getImage(path)));
    }
}