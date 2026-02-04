package com.local.assignment.data

import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

data class OtpData(
    val code: String,
    val generatedTime: Long,
    var attempts: Int = 0
)

object OtpManager {
    private const val OTP_EXPIRY_MS = 60 * 1000L
    private const val MAX_ATTEMPTS = 3
    private const val OTP_LENGTH = 6

    private val storage = ConcurrentHashMap<String, OtpData>()

    fun generateOtp(email: String): String {
        val otp = (100000..999999).random().toString()
        val data = OtpData(
            code = otp,
            generatedTime = System.currentTimeMillis()
        )
        storage[email] = data
        Timber.d("Generated OTP for $email: $otp")
        return otp
    }

    sealed interface OtpResult {
        object Success : OtpResult
        data class Invalid(val attemptsRemaining: Int) : OtpResult
        object Expired : OtpResult
        object MaxAttemptsExceeded : OtpResult
        object NoOtpFound : OtpResult
    }

    fun validateOtp(email: String, inputOtp: String): OtpResult {
        val data = storage[email] ?: return OtpResult.NoOtpFound

        val currentTime = System.currentTimeMillis()

        if (data.attempts >= MAX_ATTEMPTS) {
            return OtpResult.MaxAttemptsExceeded
        }

        if (currentTime - data.generatedTime > OTP_EXPIRY_MS) {
            return OtpResult.Expired
        }

        if (data.code == inputOtp) {
            storage.remove(email) 
            return OtpResult.Success
        } else {
            data.attempts++
            if (data.attempts >= MAX_ATTEMPTS) {
                return OtpResult.MaxAttemptsExceeded
            }
            return OtpResult.Invalid(MAX_ATTEMPTS - data.attempts)
        }
    }
}
