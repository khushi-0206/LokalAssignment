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
            // Could add error handling for invalid email, but for now simple checking
            return
        }
        OtpManager.generateOtp(email)
        AnalyticsLogger.logOtpGenerated(email)
        _authState.value = AuthState.OtpSent(email)
    }

    fun verifyOtp(email: String, otp: String) {
        when (val result = OtpManager.validateOtp(email, otp)) {
            OtpResult.Success -> {
                AnalyticsLogger.logOtpValidationSuccess(email)
                val startTime = System.currentTimeMillis()
                _authState.value = AuthState.LoggedIn(email, startTime)
                startSessionTimer(startTime)
            }
            is OtpResult.Invalid -> {
                AnalyticsLogger.logOtpValidationFailure(email, "Invalid OTP")
                _authState.value = AuthState.OtpSent(email, error = "Invalid OTP")
            }
            is OtpResult.Expired -> {
                AnalyticsLogger.logOtpValidationFailure(email, "Expired OTP")
                _authState.value = AuthState.OtpSent(email, error = "OTP Expired")
            }
            is OtpResult.MaxAttemptsExceeded -> {
                AnalyticsLogger.logOtpValidationFailure(email, "Max Attempts Exceeded")
                _authState.value = AuthState.OtpSent(email, error = "Max attempts exceeded. Please resend.")
            }
            OtpResult.NoOtpFound -> {
                AnalyticsLogger.logOtpValidationFailure(email, "No OTP Found")
                _authState.value = AuthState.OtpSent(email, error = "Session invalid. Please resend.")
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
