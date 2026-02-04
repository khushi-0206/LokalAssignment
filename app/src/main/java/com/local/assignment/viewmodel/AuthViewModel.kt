package com.local.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.local.assignment.analytics.AnalyticsLogger
import com.local.assignment.data.OtpManager
import com.local.assignment.data.OtpManager.OtpResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _sessionDuration = MutableStateFlow(0L)
    val sessionDuration: StateFlow<Long> = _sessionDuration.asStateFlow()

    private var timerJob: Job? = null

    fun sendOtp(email: String) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return
        }
        OtpManager.generateOtp(email)
        AnalyticsLogger.logOtpGenerated(email)
        // Set expiry time to 60 seconds from now
        val expiryTime = System.currentTimeMillis() + 60000
        _authState.value = AuthState.OtpSent(email, expiryTime = expiryTime)
    }

    fun verifyOtp(email: String, otp: String) {
        val currentExpiryTime = (_authState.value as? AuthState.OtpSent)?.expiryTime ?: 0L
        
        when (val result = OtpManager.validateOtp(email, otp)) {
            OtpResult.Success -> {
                AnalyticsLogger.logOtpValidationSuccess(email)
                val startTime = System.currentTimeMillis()
                _authState.value = AuthState.LoggedIn(email, startTime)
                startSessionTimer(startTime)
            }
            is OtpResult.Invalid -> {
                AnalyticsLogger.logOtpValidationFailure(email, "Invalid OTP. ${result.attemptsRemaining} attempts remaining")
                _authState.value = AuthState.OtpSent(
                    email, 
                    error = "Invalid OTP. ${result.attemptsRemaining} attempts remaining.",
                    expiryTime = currentExpiryTime
                )
            }
            is OtpResult.Expired -> {
                AnalyticsLogger.logOtpValidationFailure(email, "Expired OTP")
                _authState.value = AuthState.OtpSent(email, error = "OTP Expired. Please resend.", expiryTime = currentExpiryTime)
            }
            is OtpResult.MaxAttemptsExceeded -> {
                AnalyticsLogger.logOtpValidationFailure(email, "Max Attempts Exceeded")
                _authState.value = AuthState.OtpSent(email, error = "Max attempts exceeded. Please resend.", expiryTime = currentExpiryTime)
            }
            OtpResult.NoOtpFound -> {
                AnalyticsLogger.logOtpValidationFailure(email, "No OTP Found")
                _authState.value = AuthState.OtpSent(email, error = "Session invalid. Please resend.", expiryTime = currentExpiryTime)
            }
        }
    }

    fun resendOtp(email: String) {
        sendOtp(email)
    }

    private fun startSessionTimer(startTime: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val duration = System.currentTimeMillis() - startTime
                _sessionDuration.value = duration
                delay(1000)
            }
        }
    }

    fun resetToLogin() {
        _authState.value = AuthState.LoggedOut
    }

    fun logout() {
        val currentState = _authState.value
        if (currentState is AuthState.LoggedIn) {
            AnalyticsLogger.logLogout(currentState.email)
        }
        timerJob?.cancel()
        _sessionDuration.value = 0
        _authState.value = AuthState.LoggedOut
    }
}
