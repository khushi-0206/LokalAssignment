package com.local.assignment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OtpScreen(
    email: String,
    error: String?,
    expiryTime: Long,
    onVerifyOtp: (String) -> Unit,
    onResendOtp: () -> Unit,
    onBackClick: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    
    // Timer logic
    var timeRemaining by remember(expiryTime) { mutableStateOf((expiryTime - System.currentTimeMillis()).coerceAtLeast(0L)) }
    
    LaunchedEffect(expiryTime) {
         while (true) {
             val remaining = expiryTime - System.currentTimeMillis()
             if (remaining <= 0) {
                 timeRemaining = 0
                 break
             }
             timeRemaining = remaining
             kotlinx.coroutines.delay(1000)
         }
    }

    val formattedTime = remember(timeRemaining) {
        val minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(timeRemaining)
        val seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFCC80)) // Orange 50 to 200
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
                val inputStream = context.assets.open("otp_image.png")
                android.graphics.BitmapFactory.decodeStream(inputStream).asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }

        if (assetBitmap != null) {
            Image(
                bitmap = assetBitmap,
                contentDescription = "OTP Placeholder",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = "Enter OTP",
            style = MaterialTheme.typography.headlineMedium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "Sent to $email",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(
                onClick = onBackClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Change", style = MaterialTheme.typography.labelLarge)
            }
        }
        
        // Expiry Timer
        Text(
            text = if (timeRemaining > 0) "Expires in: $formattedTime" else "OTP Expired",
            style = MaterialTheme.typography.bodySmall,
            color = if (timeRemaining > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            label = { Text("6-digit OTP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            // Highlighted error
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onVerifyOtp(otp) },
            enabled = otp.length == 6 && timeRemaining > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onResendOtp) {
            Text("Resend OTP")
        }
    }
}
