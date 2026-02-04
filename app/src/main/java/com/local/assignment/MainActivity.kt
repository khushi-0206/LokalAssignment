package com.local.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.local.assignment.ui.LoginScreen
import com.local.assignment.ui.OtpScreen
import com.local.assignment.ui.SessionScreen
import com.local.assignment.viewmodel.AuthState
import com.local.assignment.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authState by authViewModel.authState.collectAsState()
                    val sessionDuration by authViewModel.sessionDuration.collectAsState()

                    when (val state = authState) {
                        is AuthState.LoggedOut -> {
                            LoginScreen(
                                onSendOtp = { authViewModel.sendOtp(it) }
                            )
                        }
                        is AuthState.OtpSent -> {
                            OtpScreen(
                                email = state.email,
                                error = state.error,
                                expiryTime = state.expiryTime,
                                onVerifyOtp = { otp -> authViewModel.verifyOtp(state.email, otp) },
                                onResendOtp = { authViewModel.resendOtp(state.email) },
                                onBackClick = { authViewModel.resetToLogin() }
                            )
                        }
                        is AuthState.LoggedIn -> {
                            SessionScreen(
                                email = state.email,
                                sessionStartTime = state.sessionStartTime,
                                sessionDuration = sessionDuration,
                                onLogout = { authViewModel.logout() }
                            )
                        }
                    }
                }
            }
        }
    }
}
