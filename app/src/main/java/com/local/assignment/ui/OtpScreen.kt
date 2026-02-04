package com.local.assignment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OtpScreen(
    email: String,
    error: String?,
    onVerifyOtp: (String) -> Unit,
    onResendOtp: () -> Unit
) {
    var otp by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter OTP",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Sent to $email",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            label = { Text("6-digit OTP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onVerifyOtp(otp) },
            enabled = otp.length == 6,
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
