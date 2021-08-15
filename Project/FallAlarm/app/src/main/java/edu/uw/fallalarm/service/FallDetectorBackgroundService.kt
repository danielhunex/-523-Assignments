package edu.uw.fallalarm.service

import android.Manifest
import android.R.attr.x
import android.R.attr.y
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import edu.uw.fallalarm.database.AppDatabase
import edu.uw.fallalarm.database.ContactRepository
import edu.uw.fallalarm.database.HistoryEntity
import edu.uw.fallalarm.database.HistoryRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class FallDetectorService : Service(), SensorEventListener {

    private val TAG: String = FallDetectorService::class.java.simpleName
    private lateinit var _sensorManager: SensorManager
    private lateinit var _accelerometerSensor: Sensor
    private var _latitude: Double? = null
    private var _longitude: Double? = null
    private var _locationManager: LocationManager? = null
    private var _locationListener: LocationListener? = null
    private var _smsManager: SmsManager = SmsManager.getDefault()
    private var _database: AppDatabase? = null
    private var _contactRepository: ContactRepository? = null
    private var _historyRepository: HistoryRepository? = null
    private var _handler: Handler = Handler(Looper.getMainLooper())
    private var _previousAccelerationMagnitude: Double = 9.8
    private var _currentAccelerationMagnitude: Double = 9.8
    private val LOWER_THRESHOLD: Double = 2.5 // lower acceleration threshold in m/s2
    private val UPPER_THRESHOLD: Double = 10.0 // upper acceleration threshold in m/s2
    private var _acceleration: FloatArray = FloatArray(3)
    private var _phoneNumber: String? = null
    private var _lowerThreshholdHit = false
    private var _lowerTime: Long = 0
    private lateinit var _runnable: Runnable


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        _database = AppDatabase.getInstance(applicationContext)
        _contactRepository = ContactRepository(_database!!)
        _historyRepository = HistoryRepository(_database!!)

        var contact = _contactRepository!!.getEmergencyContact()
        //TODO: validate and show message if the user has not set a contact
        _phoneNumber = contact?.getPhoneNumber()

        _locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        _locationListener = LocationListener { location ->
            _latitude = location.latitude
            _longitude = location.longitude
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            _handler.post {
                Toast.makeText(applicationContext,
                    "GPS Permission Not granted!!",
                    Toast.LENGTH_SHORT).show()
            }
        }

        _locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
            1000,
            0.0F,
            _locationListener!!)

        _latitude =
            _locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.latitude

        _longitude =
            _locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.longitude

        _sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        _accelerometerSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        _sensorManager.registerListener(this,
            _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST)

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                notifyHeartBeat()
            }
        }, 1000, 2000)

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {


                var x: Double = _acceleration[0].toDouble()
                var y: Double = _acceleration[1].toDouble()
                var z: Double = _acceleration[2].toDouble()

                _currentAccelerationMagnitude = sqrt(x.pow(2.0) + y.pow(2.0) + z.pow(2.0))

                if (_currentAccelerationMagnitude <= LOWER_THRESHOLD && !_lowerThreshholdHit) {
                    _lowerThreshholdHit = true
                    _lowerTime = System.currentTimeMillis();
                }
                if (_lowerThreshholdHit) {
                    if (_currentAccelerationMagnitude >= UPPER_THRESHOLD) {
                        var interval = System.currentTimeMillis() - _lowerTime

                        if (interval in 500..10000) //Fall is detected
                        {
                            if (_phoneNumber != null) {

                                var smsMessage =
                                    "Your contact has fallen at location : http://maps.google.com/?q=$_latitude,$_longitude . Immediate help might be needed"
                                _smsManager.sendTextMessage(_phoneNumber,
                                    null,
                                    smsMessage,
                                    null,
                                    null)

                                updateHistory(_latitude, _longitude)

                                _lowerThreshholdHit = false
                            }
                        }
                    }
                }
            }
        }, 1000, 30)

        return START_STICKY
    }

    private fun notifyHeartBeat() {
        val intent = Intent("edu.uw.status.transfer")
        intent.putExtra("heartbeat", "1")
        sendBroadcast(intent)
    }

    private fun updateHistory(latitude: Double?, longitude: Double?) {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(Date())

        var message =
            "Fall detected at location <$latitude, $longitude> on $date"
        _historyRepository?.insertContact(HistoryEntity(message,
            Date()))
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        var sensor: Sensor? = event?.sensor
        if (sensor != null && sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event!!.values, 0, _acceleration, 0, 3)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "Cleaning up resources")
        _sensorManager.unregisterListener(this)

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            _locationManager?.removeUpdates(_locationListener!!)
        }
    }
}
