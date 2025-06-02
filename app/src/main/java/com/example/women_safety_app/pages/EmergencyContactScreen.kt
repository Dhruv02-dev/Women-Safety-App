package com.example.women_safety_app.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.women_safety_app.EmergencyContact
import com.example.women_safety_app.SafetyViewModel
@Composable
fun EmergencyContactScreen(viewModel: SafetyViewModel) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
        onResult = { uri ->
            uri?.let { contactUri ->
                val contact = retrieveContactInfo(context, contactUri)
                contact?.let {
                    val updatedContacts = viewModel.emergencyContacts.toMutableList().apply {
                        add(it)
                    }
                    viewModel.saveEmergencyContacts(updatedContacts)
                    Toast.makeText(context, "${it.name} added!", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(context, "No phone number found for this contact", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch(null)
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Emergency Contacts",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF37288)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Contact Button
        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    launcher.launch(null)
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFF37288)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Select from Contacts", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Divider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 8.dp))

        // List of Emergency Contacts
        if (viewModel.emergencyContacts.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(viewModel.emergencyContacts) { contact ->
                    ContactCard(
                        contact = contact,
                        onDelete = {
                            val updatedContacts = viewModel.emergencyContacts.toMutableList().apply {
                                remove(contact)
                            }
                            viewModel.saveEmergencyContacts(updatedContacts)
                            Toast.makeText(context, "Contact removed", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No emergency contacts added yet",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// Retrieve contact info function remains the same as previous example


// Improved contact retrieval function
@SuppressLint("Range")
fun retrieveContactInfo(context: Context, contactUri: Uri): EmergencyContact? {
    val contentResolver = context.contentResolver

    // First get the contact ID from the URI
    val contactId: String? = contactUri.lastPathSegment

    return contactId?.let { id ->
        // Query for phone numbers associated with this contact
        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(id),
            null
        )

        phoneCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                // Get display name
                val name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                ) ?: "Unknown"

                // Get phone number
                val number = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )?.replace("\\s".toRegex(), "") // Remove whitespace from number

                number?.let {
                    EmergencyContact(name, number)
                }
            } else {
                null
            }
        }
    }
}

@Composable
fun ContactCard(contact: EmergencyContact, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCF7F8)) // Light Gray
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(contact.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(contact.phoneNumber, fontSize = 16.sp, color = Color.Gray)
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Contact",
                    tint = Color.Red
                )
            }
        }
    }
}