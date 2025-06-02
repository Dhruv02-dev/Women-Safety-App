package com.example.women_safety_app.pages


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.women_safety_app.R

@Composable
fun EmergencyNumbersScreen() {
    val emergencyContacts = listOf(
        "Police" to "100",
        "Women Safety" to "1091",
        "Ambulance" to "102",
        "Fire Brigade" to "101",
        "Disaster Management" to "108",
        "Child Helpline" to "1098",
        "Senior Citizen Helpline" to "14567",
        "Mental Health Helpline" to "1800-599-0019",
        "Road Accident Emergency" to "1073",
        "Anti Poison" to "1800-116-117",
        "Railway Enquiry" to "139",
        "Tourist Helpline" to "1363",
        "Air Ambulance" to "9540161344",
        "Coastal Security" to "1093",
        "Blood Bank" to "104",
        "COVID Helpline" to "1075"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Emergency Helpline",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp),
            color = Color.Black
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(emergencyContacts) { (name, number) ->
                EmergencyContactCard(name, number)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EmergencyContactCard(name: String, phoneNumber: String) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDEB)) // Light peach background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Call $phoneNumber",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
               // Text(text = "Make Call", color = Color.Black)
                //Icon(painter = painterResource(id = R.drawable.baseline_call_24),contentDescription = null)

            Icon(imageVector = Icons.Default.Phone, contentDescription = null,modifier = Modifier.size(22.dp), tint = Color.Black )
            }
        }
    }
}
