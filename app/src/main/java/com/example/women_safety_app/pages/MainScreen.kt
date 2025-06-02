package com.example.women_safety_app.pages

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import com.example.women_safety_app.AuthState
import com.example.women_safety_app.AuthViewModel
import com.example.women_safety_app.EmergencyContact
import com.example.women_safety_app.R
import com.example.women_safety_app.SafetyViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: SafetyViewModel
) {

    TransparentSystemBars()

    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("Login")
            else -> Unit
        }
    }

    GradientBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .background(Color(0xFFFF3D00))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Are you in Emergency?\nPress below to send an SOS to your emergency contacts.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            }



            Spacer(modifier = Modifier.height(30.dp))

            // SOS Button (Centered)
            SOSButtons {
                if (viewModel.emergencyContacts.isNotEmpty()) {
                    getCurrentLocation(context) { location ->
                        val message = "Emergency! Need help. My location: $location"
                        viewModel.emergencyContacts.forEachIndexed { index, contact ->
                            if (index == 0) {
                                makeEmergencyCall(context, contact.phoneNumber)
                                sendSOSMessage(context, contact.phoneNumber, message)
                            } else {
                                sendSOSMessage(context, contact.phoneNumber, message)
                            }
                        }
                        // startLiveTracking(context, viewModel.emergencyContacts)
                    }
                } else {
                    Toast.makeText(context, "Please add emergency contacts first", Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Shake Detection & Live Tracking Buttons (Side by Side)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Shake Detection Button
                Button(
                    onClick = { viewModel.updateShakeDetectionEnabled(!viewModel.isShakeDetectionEnabled) },
                    modifier = Modifier
                        .width(90.dp)
                        .height(100.dp)
                        .border(
                            2.dp,
                            if (viewModel.isShakeDetectionEnabled) Color(0xFF42A842) else Color(
                                0xFFE91E63
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    //elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning, // Changed from Call to Vibration
                            contentDescription = "Shake Detect",
                            modifier = Modifier.size(32.dp),
                            tint = if (viewModel.isShakeDetectionEnabled) Color(0xFF42A842) else Color(0xFFE91E63)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Alert",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (viewModel.isShakeDetectionEnabled) Color(0xFF42A842) else Color(0xFFE91E63)
                        )
                    }
                }

                //Spacer(modifier = modifier.padding(start = 1.dp))

                // Siren Button
                // val context = LocalContext.current
                var isSirenPlaying by remember { mutableStateOf(false) }
                var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

                Button(
                    onClick = {
                        isSirenPlaying = !isSirenPlaying
                        if (isSirenPlaying) {
                            mediaPlayer?.release()
                            mediaPlayer = MediaPlayer.create(context, R.raw.siren).apply {
                                isLooping = true
                                start()
                            }
                        } else {
                            mediaPlayer?.stop()
                            mediaPlayer?.release()
                            mediaPlayer = null
                        }
                    },
                    modifier = Modifier
                        .width(90.dp)
                        .height(100.dp)
                        .border(
                            2.dp,
                            if (isSirenPlaying) Color(0xFF42A842) else Color(0xFFE91E63),
                            RoundedCornerShape(12.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    //elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSirenPlaying) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
                            contentDescription = "Siren",
                            modifier = Modifier.size(32.dp),
                            tint = if (isSirenPlaying) Color(0xFF42A842) else Color(0xFFE91E63)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSirenPlaying) "Siren" else "Siren",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSirenPlaying) Color(0xFF42A842) else Color(0xFFE91E63)
                        )
                    }
                }


                // Spacer(modifier = modifier.padding(horizontal = 2.dp))
                // Live Tracking Button
                Button(
                    onClick = {
                        if (viewModel.isLiveTrackingEnabled) {
                            stopLiveTracking(context)
                        } else {
                            startLiveTracking(context, viewModel.emergencyContacts)
                        }
                        viewModel.updateLiveTrackingEnabled(!viewModel.isLiveTrackingEnabled)
                    },
                    modifier = Modifier
                        .width(90.dp)
                        .height(100.dp)
                        .border(
                            2.dp,
                            if (viewModel.isLiveTrackingEnabled) Color(0xFF42A842) else Color(
                                0xFFE91E63
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    // elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Live Tracking",
                            modifier = Modifier.size(32.dp),
                            tint = if (viewModel.isLiveTrackingEnabled) Color(0xFF42A842) else Color(0xFFE91E63)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Track", // Shortened text
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (viewModel.isLiveTrackingEnabled) Color(0xFF42A842) else Color(0xFFE91E63)
                        )
                    }
                }

                DisposableEffect(Unit) {
                    onDispose {
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }
                }
            }
        }
    }
}



@Composable
fun SOSButtons(onClick: () -> Unit) {
//CHANGED SHADES
    Card(
        modifier = Modifier
            .padding(4.dp)
            .height(350.dp)
            .width(450.dp)
            .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFCFC), // Light Pink
                            Color(0xFFFFA1C0),
                            Color(0xFFFD8AB5), // Medium Pink
                            // Color(0xFFEC7190)  // Dark Pink
                        )
                    )
                ),
        contentAlignment = Alignment.Center

        ) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFA3671))
                    .border(4.dp, Color.Transparent, CircleShape)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF255E))
                        .border(4.dp, Color.Transparent, CircleShape)
                        .clickable(onClick = onClick)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE80044))
                            .border(16.dp, Color.Transparent, CircleShape)
                            .clickable(onClick = onClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SOS",
                            fontSize = 70.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}





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


@Composable
fun GradientBackground(content: @Composable ()-> Unit){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF), // Light Pink
                        Color(0xFFFDFDFD),
                        Color(0xFFFFFCFC), // Medium Pink
                        // Color(0xFFEC7190)  // Dark Pink
                    )
                )
            )
    ){
        content()
    }
}

@Composable
fun TransparentSystemBars() {
    val context = LocalContext.current
    val window = (context as Activity).window
    val systemUiController = rememberSystemUiController()

    SideEffect {
        // Set the status bar and navigation bar to transparent
        window.statusBarColor = Color(0xFFF37288).toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

        // Enable edge-to-edge mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        systemUiController.isSystemBarsVisible = true
    }
}

