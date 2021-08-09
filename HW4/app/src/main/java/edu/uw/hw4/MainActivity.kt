package edu.uw.hw4

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var _sensorManager: SensorManager
    private lateinit var _accelerometer: Sensor
    private lateinit var _textViewCounter: TextView;
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _textViewCounter = findViewById(R.id.textview_counter)
        _sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        _sensorManager.registerListener(this,_accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    override fun onDestroy() {
        super.onDestroy()
        _sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        val alpha: Float = 0.8f
        if (event != null) {

            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                var x: Double = event.values[0].toDouble()
                var y: Double = event.values[1].toDouble()
                var z: Double = event.values[2].toDouble()

                var mag = sqrt(x.pow(2.0) + y.pow(2.0) + z.pow(2.0))
                //   history.add(mag)
                // binding.textHome.text = mag.toString()

            }
            /*    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                    // Isolate the force of gravity with the low-pass filter.
                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                    // Remove the gravity contribution with the high-pass filter.
                    linear_acceleration[0] = event.values[0] - gravity[0]
                    linear_acceleration[1] = event.values[1] - gravity[1]
                    linear_acceleration[2] = event.values[2] - gravity[2]

                    var magnitude = Math.sqrt(
                        Math.pow(
                            linear_acceleration[0].toDouble(), 2.0)+
                            Math.pow(linear_acceleration[1].toDouble(), 2.0)+
                            Math.pow(linear_acceleration[2].toDouble(), 2.0))

                               binding.textHome.text =
                            "x: ${linear_acceleration[0]}, y:${linear_acceleration[1]}, z:${linear_acceleration[2]}   Magnitude: ${magnitude}"
                }*/
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }
}