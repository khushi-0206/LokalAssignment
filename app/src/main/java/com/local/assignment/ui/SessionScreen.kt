package com.local.assignment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.TimeZone

@Composable
fun SessionScreen(
    email: String,
    sessionStartTime: Long,
    sessionDuration: Long,
    onLogout: () -> Unit
) {
    val formattedStartTime = remember(sessionStartTime) {
        SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(sessionStartTime))
    }

    val formattedDuration = remember(sessionDuration) {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(sessionDuration)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(sessionDuration) % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F2F1), Color(0xFF80CBC4))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val assetBitmap = remember(context) {
            try {
                val inputStream = context.assets.open("session_image.png")
                android.graphics.BitmapFactory.decodeStream(inputStream).asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }

        if (assetBitmap != null) {
            Image(
                bitmap = assetBitmap,
                contentDescription = "Session Placeholder",
                modifier = Modifier.size(100.dp)
            )
        } else {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Person,
                contentDescription = "Session Placeholder",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome, $email",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Session Started: $formattedStartTime")
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Duration: $formattedDuration",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
    }
}
