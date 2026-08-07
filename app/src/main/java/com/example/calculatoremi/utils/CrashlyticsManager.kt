package com.example.calculatoremi.utils

import android.util.Log
import com.example.calculatoremi.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashlyticsManager {

    private const val TAG = "CrashlyticsManager"

    private val crashlytics: FirebaseCrashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    /**
     * Enable or disable Crashlytics data collection dynamically.
     */
    fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

    /**
     * Logs a custom message to Crashlytics log buffer.
     */
    fun log(message: String) {
        Log.d(TAG, message)
        crashlytics.log(message)
    }

    /**
     * Records a non-fatal exception to Crashlytics.
     */
    fun recordException(throwable: Throwable) {
        Log.e(TAG, "Recording non-fatal exception", throwable)
        crashlytics.recordException(throwable)
    }

    /**
     * Sets custom key-value metadata for crash reports.
     */
    fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Long) {
        crashlytics.setCustomKey(key, value)
    }

    /**
     * Identifies a user in Crashlytics reports (e.g. user ID or anonymous ID).
     */
    fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    /**
     * Throws a RuntimeException to test Crashlytics integration.
     * Note: Only invoke this explicitly for testing purposes!
     */
    fun testCrash() {
        if (BuildConfig.DEBUG) {
            log("Triggering test crash for Firebase Crashlytics verification.")
            throw RuntimeException("Test Crash - Firebase Crashlytics Verification")
        }
    }
}
