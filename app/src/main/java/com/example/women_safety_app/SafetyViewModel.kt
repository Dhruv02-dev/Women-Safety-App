package com.example.women_safety_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlin.math.sqrt

// ViewModel for managing shared data
class SafetyViewModel(private val context: Context) : ViewModel(), SensorEventListener {
    private val sharedPreferencesHelper = SharedPreferencesHelper(context)

    var emergencyContacts by mutableStateOf(sharedPreferencesHelper.getEmergencyContacts())
        private set

    fun saveEmergencyContacts(contacts: List<EmergencyContact>) {
        sharedPreferencesHelper.saveEmergencyContacts(contacts)
        emergencyContacts = contacts
    }



    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var shakeThreshold = 20f // Sensitivity level for shake detection
    private var lastShakeTime = 0L

    init {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
        if (acceleration > shakeThreshold) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastShakeTime > 3000) { // Avoid multiple triggers
                lastShakeTime = currentTime
                triggerSOS() // Call the function to send SOS alert
            }
        }
    }

    private fun triggerSOS() {
        Toast.makeText(context, "Shake detected! Sending SOS alert...", Toast.LENGTH_SHORT).show()

        getCurrentLocation(context) { location ->
            val message = "Emergency! Need help. My location: $location"

            if (emergencyContacts.isNotEmpty()) {
                makeEmergencyCall(context, emergencyContacts[0].phoneNumber) // Call first contact
                emergencyContacts.forEach { contact ->
                    sendSOSMessage(context, contact.phoneNumber, message) // Send SOS message
                }
            } else {
                Toast.makeText(context, "No emergency contacts found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun unregisterShakeListener() {
        sensorManager.unregisterListener(this)
    }


    // State for live location tracking
    var isLiveTrackingEnabled by mutableStateOf(false)
        private set // Prevent external modification

    // State for shake detection
    var isShakeDetectionEnabled by mutableStateOf(true)
        private set // Prevent external modification

    // Function to enable/disable live location tracking
    fun updateLiveTrackingEnabled(enabled: Boolean) {
        isLiveTrackingEnabled = enabled
        if (enabled) {
            startLiveTracking(context, emergencyContacts)
            Toast.makeText(context, "Live Tracking: ON", Toast.LENGTH_SHORT).show()
        } else {
            stopLiveTracking(context)
            Toast.makeText(context, "Live Tracking: OFF", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to enable/disable shake detection
    fun updateShakeDetectionEnabled(enabled: Boolean) {
        isShakeDetectionEnabled = enabled
        if (enabled) {
           // startLiveTracking(context, emergencyContacts)
            accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            Toast.makeText(context, "Shake Detection: ON", Toast.LENGTH_SHORT).show()
        } else {
          //  stopLiveTracking(context)
            sensorManager.unregisterListener(this)
            Toast.makeText(context, "Shake Detection: OFF", Toast.LENGTH_SHORT).show()
        }
    }



}




class SafetyViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SafetyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SafetyViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class EmergencyContact(
    val name: String,
    val phoneNumber: String
)








private fun getCurrentLocation(context: Context, onLocationReceived: (String) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val locationText = "https://maps.google.com/?q=${it.latitude},${it.longitude}"
                onLocationReceived(locationText)
            } ?: onLocationReceived("Location not available")
        }
    } else {
        onLocationReceived("Location permission denied")
    }
}

private fun sendSOSMessage(context: Context, phoneNumber: String, message: String) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
        == PackageManager.PERMISSION_GRANTED) {

        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        Toast.makeText(context, "SOS Message Sent!", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "SMS Permission Denied!", Toast.LENGTH_SHORT).show()
    }
}

private fun makeEmergencyCall(context: Context, phoneNumber: String) {
    val callIntent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
        == PackageManager.PERMISSION_GRANTED) {
        context.startActivity(callIntent)
    } else {
        Toast.makeText(context, "Call Permission Denied!", Toast.LENGTH_SHORT).show()
    }
}

private var isTracking = false
private  var locationCallback: LocationCallback? = null

private fun startLiveTracking(context: Context, contacts: List<EmergencyContact>) {
    // Check if tracking is already active
    if (isTracking) {
        stopLiveTracking(context)
        Log.d("LiveTracking", "Live tracking is already active")
        Toast.makeText(context, "Live tracking is already active", Toast.LENGTH_SHORT).show()
        return
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Check location permissions
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        Log.d("LiveTracking", "Location permission denied")
        return
    }

    // Create location request
    val locationRequest = LocationRequest.create().apply {
        interval = 10000 // 10 seconds
        fastestInterval = 5000 // 5 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    // Initialize location callback
    locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (isTracking) { // Only send updates if tracking is active
                locationResult.locations.lastOrNull()?.let { location ->
                    val locationText = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                    val message = "Emergency! Live tracking active. My location: $locationText"

                    // Send SOS message to all emergency contacts
                    contacts.forEach { contact ->
                        sendSOSMessage(context, contact.phoneNumber, message)
                    }
                }
            }
        }
    }

    // Request location updates
    try {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
        isTracking = true
        Log.d("LiveTracking", "Live tracking started successfully")
        Toast.makeText(context, "Live tracking started", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("LiveTracking", "Error starting live tracking: ${e.message}")
        Toast.makeText(context, "Error starting live tracking", Toast.LENGTH_SHORT).show()
    }
}


private fun stopLiveTracking(context: Context) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (locationCallback != null) {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback!!)
            locationCallback = null // Reset the callback
            isTracking = false
            Log.d("LiveTracking", "Live tracking stopped successfully")
            Toast.makeText(context, "Live tracking stopped", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("LiveTracking", "Error stopping live tracking: ${e.message}")
            Toast.makeText(context, "Error stopping live tracking", Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.d("LiveTracking", "Live tracking is not active (callback is null)")
        Toast.makeText(context, "Live tracking is not active", Toast.LENGTH_SHORT).show()
    }
}

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("EmergencyPrefs", Context.MODE_PRIVATE)

    // Save a list of emergency contacts as a JSON string
    fun saveEmergencyContacts(contacts: List<EmergencyContact>) {
        val json = Gson().toJson(contacts)
        sharedPreferences.edit().putString("EMERGENCY_CONTACTS", json).apply()
    }

    // Retrieve the list of emergency contacts
    fun getEmergencyContacts(): List<EmergencyContact> {
        val json = sharedPreferences.getString("EMERGENCY_CONTACTS", null)
        return if (json != null) {
            Gson().fromJson(json, Array<EmergencyContact>::class.java).toList()
        } else {
            emptyList()
        }
    }
}
