package com.local.assignment.analytics

import timber.log.Timber

object AnalyticsLogger {

    fun logOtpGenerated(email: String) {
        Timber.i("EVENT: OTP_GENERATED | Email: $email")
    }

    fun logOtpValidationSuccess(email: String) {
        Timber.i("EVENT: OTP_VALIDATION_SUCCESS | Email: $email")
    }

    fun logOtpValidationFailure(email: String, reason: String) {
        Timber.w("EVENT: OTP_VALIDATION_FAILURE | Email: $email | Reason: $reason")
    }

    fun logLogout(email: String) {
        Timber.i("EVENT: LOGOUT | Email: $email")
    }
}
