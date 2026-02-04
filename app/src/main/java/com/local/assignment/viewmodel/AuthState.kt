package com.local.assignment.viewmodel

sealed interface AuthState {
    data object LoggedOut : AuthState
    data class OtpSent(val email: String, val error: String? = null, val expiryTime: Long = 0L) : AuthState
    data class LoggedIn(val email: String, val sessionStartTime: Long) : AuthState
}
