package com.local.assignment.data

import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

data class OtpData(
    val code: String,
    val generatedTime: Long,
    var attempts: Int = 0
)

object OtpManager {
    private const val OTP_EXPIRY_MS = 60 * 1000L // 60 seconds
    private const val MAX_ATTEMPTS = 3
    private const val OTP_LENGTH = 6

    // Using ConcurrentHashMap for thread safety, though strictly local usage might not need it.
    private val storage = ConcurrentHashMap<String, OtpData>()

    fun generateOtp(email: String): String {
        // Generate random 6 digit numeric code
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
        object Invalid : OtpResult
        object Expired : OtpResult
        object MaxAttemptsExceeded : OtpResult
        object NoOtpFound : OtpResult
    }

    fun validateOtp(email: String, inputOtp: String): OtpResult {
        val data = storage[email] ?: return OtpResult.NoOtpFound

        val currentTime = System.currentTimeMillis()

        // Check if attempts exceeded first (though usually we check on failure, strict rule: reset only on new OTP)
        if (data.attempts >= MAX_ATTEMPTS) {
            return OtpResult.MaxAttemptsExceeded
        }

        // Check expiry
        if (currentTime - data.generatedTime > OTP_EXPIRY_MS) {
            return OtpResult.Expired
        }

        if (data.code == inputOtp) {
            // Success
            // Can optionally clear OTP here to prevent re-use
            storage.remove(email) 
            return OtpResult.Success
        } else {
            // Increment attempts
            data.attempts++
            if (data.attempts >= MAX_ATTEMPTS) {
                return OtpResult.MaxAttemptsExceeded
            }
            return OtpResult.Invalid
        }
    }
}
