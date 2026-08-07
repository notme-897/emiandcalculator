package com.example.calculatoremi.utils

import android.util.Log
import com.example.calculatoremi.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException

object RemoteConfigManager {

    private const val TAG = "RemoteConfigManager"

    // Default configuration keys & fallback values
    private val defaultDefaults: Map<String, Any> = mapOf(
        "welcome_banner_message" to "Welcome to EMI & Financial Calculator!",
        "enable_new_loan_tools" to true,
        "min_app_version" to 1L
    )

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    /**
     * Initializes Firebase Remote Config with default values and fetches latest config from server.
     */
    fun init(onComplete: ((Boolean) -> Unit)? = null) {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0L else 3600L)
            .build()

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(defaultDefaults)

        fetchAndActivate(onComplete)
        listenForRealtimeUpdates()
    }

    /**
     * Fetches and activates Remote Config values.
     */
    fun fetchAndActivate(onComplete: ((Boolean) -> Unit)? = null) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                val isSuccess = task.isSuccessful
                if (isSuccess) {
                    Log.d(TAG, "Remote Config fetched and activated successfully.")
                } else {
                    Log.e(TAG, "Remote Config fetch failed.", task.exception)
                }
                onComplete?.invoke(isSuccess)
            }
    }

    /**
     * Listens for real-time config updates pushed from Firebase.
     */
    private fun listenForRealtimeUpdates() {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Updated keys: ${configUpdate.updatedKeys}")
                remoteConfig.activate().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Real-time Remote Config activated.")
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error: ${error.code}", error)
            }
        })
    }

    fun getString(key: String, fallback: String = ""): String {
        val value = remoteConfig.getString(key)
        return value.ifEmpty { fallback }
    }

    fun getBoolean(key: String, fallback: Boolean = false): Boolean {
        return try {
            remoteConfig.getBoolean(key)
        } catch (e: Exception) {
            fallback
        }
    }

    fun getLong(key: String, fallback: Long = 0L): Long {
        return try {
            remoteConfig.getLong(key)
        } catch (e: Exception) {
            fallback
        }
    }

    fun getDouble(key: String, fallback: Double = 0.0): Double {
        return try {
            remoteConfig.getDouble(key)
        } catch (e: Exception) {
            fallback
        }
    }
}
