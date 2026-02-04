package com.local.assignment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun LoginScreen(
    onSendOtp: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val assetBitmap = remember(context) {
            try {
                val inputStream = context.assets.open("login_image.png")
                android.graphics.BitmapFactory.decodeStream(inputStream).asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }


        if (assetBitmap != null) {
            Image(
                bitmap = assetBitmap,
                contentDescription = "Login Placeholder",
                modifier = Modifier.size(100.dp)
            )
        } else {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                contentDescription = "Login Placeholder",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { 
                if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    android.widget.Toast.makeText(context, "Please enter a valid email address", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    onSendOtp(email) 
                }
            },
            enabled = email.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send OTP")
        }
    }
}
