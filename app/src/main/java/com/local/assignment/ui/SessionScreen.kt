package com.local.assignment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
