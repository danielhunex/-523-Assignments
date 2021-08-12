package edu.uw.hw4

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var _sensorManager: SensorManager
    private lateinit var _accelerometer: Sensor
    private lateinit var _textViewCounter: TextView
    private lateinit var _textViewShake: TextView
    private var _prevMagnitude: Double = 0.0
    private var _counter: Int = 0
    private var _gravity = FloatArray(3) { 0.0F }
    private var _linearAcceleration = FloatArray(3) { 0.0F }
    private lateinit var _btnSave: Button
    private var _shakingHappend = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _textViewCounter = findViewById(R.id.textview_counter)
        _textViewShake = findViewById(R.id.textview_shake)
        _btnSave = findViewById(R.id.btn_save)

        _sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_UI)

        _btnSave.isEnabled = _shakingHappend
        _textViewShake.isVisible = !_shakingHappend
        _btnSave.setOnClickListener()
        {
            _shakingHappend = false //stop counting
            _counter = 0
           _textViewShake.isVisible =!_shakingHappend
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        val alpha: Float = 0.8f
        if (event != null) {

            if (!_shakingHappend) { // Isolate the force of gravity with the low-pass filter.
                _gravity[0] = alpha * _gravity[0] + (1 - alpha) * event.values[0]
                _gravity[1] = alpha * _gravity[1] + (1 - alpha) * event.values[1]
                _gravity[2] = alpha * _gravity[2] + (1 - alpha) * event.values[2]

                // Remove the gravity contribution with the high-pass filter.
                _linearAcceleration[0] = event.values[0] - _gravity[0]
                _linearAcceleration[1] = event.values[1] - _gravity[1]
                _linearAcceleration[2] = event.values[2] - _gravity[2]

                var magnitude = sqrt(
                    _linearAcceleration[0].toDouble().pow(2.0) +
                            _linearAcceleration[1].toDouble().pow(2.0) +
                            _linearAcceleration[2].toDouble().pow(2.0)
                )

                if (magnitude - _prevMagnitude > 12) {
                    _shakingHappend = true
                } else {
                    _prevMagnitude = magnitude
                }
                _btnSave.isEnabled = _shakingHappend
                _textViewShake.isVisible= !_shakingHappend

            } else

                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                    // Isolate the force of gravity with the low-pass filter.
                    _gravity[0] = alpha * _gravity[0] + (1 - alpha) * event.values[0]
                    _gravity[1] = alpha * _gravity[1] + (1 - alpha) * event.values[1]
                    _gravity[2] = alpha * _gravity[2] + (1 - alpha) * event.values[2]

                    // Remove the gravity contribution with the high-pass filter.
                    _linearAcceleration[0] = event.values[0] - _gravity[0]
                    _linearAcceleration[1] = event.values[1] - _gravity[1]
                    _linearAcceleration[2] = event.values[2] - _gravity[2]

                    var magnitude = sqrt(
                        _linearAcceleration[0].toDouble().pow(2.0) +
                                _linearAcceleration[1].toDouble().pow(2.0) +
                                _linearAcceleration[2].toDouble().pow(2.0)
                    )
                    if (_prevMagnitude < 1.0 && magnitude > 1.5) { //I did sampling to find these thresholds over historical data I collected
                        _counter++
                    }
                    _prevMagnitude = magnitude
                    _textViewCounter.text = _counter.toString()
                }
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}