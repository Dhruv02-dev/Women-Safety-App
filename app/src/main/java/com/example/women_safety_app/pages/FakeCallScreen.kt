package com.example.women_safety_app.pages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat

import androidx.compose.runtime.DisposableEffect

import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.women_safety_app.MainActivity
import com.example.women_safety_app.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.*


// GO TO LINE 462 FOR ERROR
@Composable
fun FakeCallScreen(onFakeCallButtonPressed: () -> Unit, onFakeCallEnded: () -> Unit) {
    val context = LocalContext.current
    var showCallScreen by remember { mutableStateOf(false) }
    var showInCallScreen by remember { mutableStateOf(false) }  // Added state for InCallScreen
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.ringtone) }
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var callerName by remember { mutableStateOf("") }
    var delayTime by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8E8E8))
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Tap the button to trigger a fake call and escape uncomfortable or dangerous situations.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                modifier = Modifier.padding(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = callerName,
            onValueChange = { callerName = it },
            label = { Text("Enter Fake Caller Name", fontSize = 13.sp) },
            placeholder = { Text("If left empty, a random number will appear", fontSize = 12.sp, color = Color.Gray) }
        )
        Spacer(modifier = Modifier.height(2.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = delayTime,
            onValueChange = { delayTime = it },
            label = { Text("Time before the fake call rings (in seconds)", fontSize = 12.sp) },
            placeholder = { Text("Default: 3 seconds", fontSize = 12.sp, color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(80.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .shadow(10.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFFE91E63))
                .clickable {
                    coroutineScope.launch {
                        val delayInMillis = (delayTime.toIntOrNull() ?: 3) * 1000L // Convert to milliseconds, default 3 sec
                        delay(delayInMillis) // User-defined delay before ringtone & vibration

                        mediaPlayer.start()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                    500,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
                            vibrator.vibrate(500)
                        }

                        delay(2000) // Additional 2-second delay before showing the call screen
                        showCallScreen = true
                        onFakeCallButtonPressed()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Fake Call",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }
    }

    if (showCallScreen && !showInCallScreen) {
        FakeCallUI(
            callerName = if (callerName.isBlank()) "9" + (100000000..999999999).random().toString() else callerName,
            onAnswer = {
                mediaPlayer.stop()
                vibrator.cancel()
                Toast.makeText(context, "Call Answered", Toast.LENGTH_SHORT).show()

                showCallScreen = false
                showInCallScreen = true
            },
            onDecline = {
                mediaPlayer.stop()
                vibrator.cancel()
                Toast.makeText(context, "Call Declined", Toast.LENGTH_SHORT).show()
                showCallScreen = false
                onFakeCallEnded()
            }
        )
    }


    if (showInCallScreen) {
        InCallScreen(
            callerName = if (callerName.isBlank()) "9" + (100000000..999999999).random().toString() else callerName,
            onEndCall = {
                showInCallScreen = false
                onFakeCallEnded()
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}

@Composable
fun FakeCallUI(callerName: String,onAnswer: () -> Unit, onDecline: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF001f3d), Color(0xFF000000)) // Dark blue to black gradient
                )
            ) // Darker gray background
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Caller Image
            Image(
                painter = painterResource(id = R.drawable.fake_caller),
                contentDescription = "Caller Image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                colorFilter = ColorFilter.tint(Color.White)

            )

            Spacer(modifier = Modifier.height(16.dp))

            // Caller Name
            Text(
                text =callerName,
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom

            ) {
                // Decline Button
                Button(
                    onClick = onDecline,
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    shape = CircleShape,
                    modifier = Modifier.size(100.dp) // Increased button size
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.call_end_icon),
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
                // Answer Button
                Button(
                    onClick = onAnswer,
                    colors = ButtonDefaults.buttonColors(Color.Green),
                    shape = CircleShape,
                    modifier = Modifier.size(100.dp)

                ) {
                    Icon(Icons.Filled.Call, contentDescription = "Answer", tint = Color.White, modifier = Modifier.size(60.dp)) // Larger icon size
                }
            }
        }
    }
}
@Composable
fun InCallScreen(callerName: String, onEndCall: () -> Unit) {
    var timeElapsed by remember { mutableStateOf(0) } // Variable to track time
    val isInCall = remember { mutableStateOf(true) } // Keep track if the call is ongoing

    // Start a timer when the InCallScreen is activated
    LaunchedEffect(isInCall.value) {
        while (isInCall.value) {
            delay(1000) // Update every second
            timeElapsed += 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF001f3d), Color(0xFF000000)) // Dark blue to black gradient
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp), // Pushes call controls down
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                painter = painterResource(id = R.drawable.fake_caller),
                contentDescription = "Caller",
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = callerName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = String.format("%02d:%02d", timeElapsed / 60, timeElapsed % 60),
                fontSize = 24.sp,
                color = Color.Gray
            )
        }

        // Icons and Call Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ======= Icons Row =======
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mute_icon),
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )

                Icon(
                    painter = painterResource(id = R.drawable.message_icon),
                    contentDescription = "Video Call",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )

                Icon(
                    painter = painterResource(id = R.drawable.keyboard_icon),
                    contentDescription = "Add Call",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(70.dp))

            // ======= Call End Button Row =======
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speaker Icon on the left
                Icon(
                    painter = painterResource(id = R.drawable.add_call),
                    contentDescription = "Speaker",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.width(60.dp))

                // Call End Button in the center
                Button(
                    onClick = {
                        isInCall.value = false // Stop the timer
                        onEndCall()
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    shape = CircleShape,
                    modifier = Modifier.size(90.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.call_end_icon),
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.width(60.dp))

                // Keyboard Icon on the right
                Icon(
                    painter = painterResource(id = R.drawable.speaker),
                    contentDescription = "Keyboard",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}




private fun triggerFakeCall(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "fake_call_channel",
            "Fake Call",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for fake calls"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {  // Opens MainActivity when clicked
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val notification = NotificationCompat.Builder(context, "fake_call_channel")
        .setSmallIcon(androidx.core.R.drawable.ic_call_answer)//WHAT IS THIS
        .setContentTitle("Incoming Call")
        .setContentText("Fake Caller")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .setFullScreenIntent(pendingIntent, true)
        .build()

    notificationManager.notify(1, notification)
}
