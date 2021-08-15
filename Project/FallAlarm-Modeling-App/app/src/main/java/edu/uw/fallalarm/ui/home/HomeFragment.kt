package edu.uw.fallalarm.ui.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.uw.fallalarm.R
import edu.uw.fallalarm.databinding.FragmentHomeBinding
import java.io.File
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sqrt

class HomeFragment : Fragment(), SensorEventListener {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private var gravity = FloatArray(3) { 0F }
    private var linear_acceleration = FloatArray(3) { 0F }
    private var resume = true
    private lateinit var _sensorManager: SensorManager
    private lateinit var _accelerometer: Sensor
    private  var history: MutableList<Double> = mutableListOf()
private var _time:Long =0
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        binding.btnSave.setOnClickListener {

            val mediaStorageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (mediaStorageDir != null && !mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
               Log.d("data","Unable to create folder ${mediaStorageDir}")
            }
            Log.d("Photo Directory", mediaStorageDir?.absolutePath + "")
            //create file for the picture
            var file = File(mediaStorageDir?.absolutePath + "/data.txt")


            file.printWriter().use { out ->
                history.forEach {
                    out.println("$it")
                }
            }
        }

        _sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        val alpha: Float = 0.8f
        var diff = System.currentTimeMillis() -_time
        if (event != null && diff>1000) {

            if(event.sensor.type == Sensor.TYPE_ACCELEROMETER)
            {
                var x:Double = event.values[0].toDouble()
                var y:Double= event.values   [1].toDouble()
                var z:Double = event.values[2].toDouble()

                var mag = sqrt(x.pow(2.0) + y.pow(2.0) + z.pow(2.0))
                history.add(mag)
                binding.textHome.text = mag.toString()
                _time = System.currentTimeMillis()

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
        Log.d("Not yet implemented", "Not implemented")
    }

    override fun onResume() {
        super.onResume()
        _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        _sensorManager.unregisterListener(this)
    }

    fun resumeReading(view: View) {
        this.resume = true
    }

    fun pauseReading(view: View) {
        this.resume = false
    }
}