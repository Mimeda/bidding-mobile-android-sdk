package com.mimeda.sdk.events

import android.content.Context
import com.mimeda.sdk.api.ApiService
import com.mimeda.sdk.events.PerformanceEventParams
import com.mimeda.sdk.events.PerformanceEventType
import com.mimeda.sdk.utils.DeviceInfo
import com.mimeda.sdk.utils.Logger
import com.mimeda.sdk.utils.SecurePreferences
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class EventTracker(
    private val apiService: ApiService,
    private val context: Context
) {
    private val executor = Executors.newSingleThreadExecutor()
    
    companion object {
        private const val SESSION_DURATION_MS = 30 * 60 * 1000L
        private const val PREFS_NAME = "mimeda_sdk_session"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_SESSION_TIMESTAMP = "session_timestamp"
        private const val KEY_ANONYMOUS_ID = "anonymous_id"
        private const val SHUTDOWN_TIMEOUT_SECONDS = 5L
    }

    private fun getOrCreateSessionId(): String {
        return try {
            val currentTime = System.currentTimeMillis()
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedSessionId = SecurePreferences.getString(prefs, KEY_SESSION_ID, null)
            val savedTimestamp = prefs.getLong(KEY_SESSION_TIMESTAMP, 0L)
            
            if (savedSessionId == null || (currentTime - savedTimestamp) > SESSION_DURATION_MS) {
                val newSessionId = java.util.UUID.randomUUID().toString()
                val editor = prefs.edit()
                SecurePreferences.putString(editor, KEY_SESSION_ID, newSessionId)
                editor.putLong(KEY_SESSION_TIMESTAMP, currentTime)
                editor.apply()
                newSessionId
            } else {
                savedSessionId
            }
        } catch (e: Exception) {
            Logger.e("Failed to get or create session ID from SharedPreferences", e)
            java.util.UUID.randomUUID().toString()
        }
    }

    private fun getOrCreateAnonymousId(): String {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedAnonymousId = SecurePreferences.getString(prefs, KEY_ANONYMOUS_ID, null)
            
            if (savedAnonymousId == null) {
                val newAnonymousId = java.util.UUID.randomUUID().toString()
                val editor = prefs.edit()
                SecurePreferences.putString(editor, KEY_ANONYMOUS_ID, newAnonymousId)
                editor.apply()
                newAnonymousId
            } else {
                savedAnonymousId
            }
        } catch (e: Exception) {
            Logger.e("Failed to get or create anonymous ID from SharedPreferences", e)
            java.util.UUID.randomUUID().toString()
        }
    }

    fun track(
        eventName: EventName,
        eventParameter: EventParameter,
        params: EventParams,
        eventType: EventType
    ) {
        try {
            executor.execute {
                try {
                    val sessionId = getOrCreateSessionId()
                    val anonymousId = getOrCreateAnonymousId()
                    
                    apiService.trackEvent(
                        eventName = eventName,
                        eventParameter = eventParameter,
                        params = params,
                        eventType = eventType,
                        appName = DeviceInfo.getAppName(),
                        deviceId = DeviceInfo.getDeviceId(),
                        os = DeviceInfo.getOs(),
                        language = DeviceInfo.getLanguage(),
                        sessionId = sessionId,
                        anonymousId = anonymousId
                    )
                } catch (e: Exception) {
                    Logger.e("An error occurred in event tracker thread", e)
                }
            }
        } catch (e: Exception) {
            Logger.e("Failed to submit event to executor", e)
        }
    }
    
    fun trackPerformance(
        eventType: PerformanceEventType,
        params: PerformanceEventParams
    ) {
        try {
            executor.execute {
                try {
                    val sessionId = getOrCreateSessionId()
                    val anonymousId = getOrCreateAnonymousId()
                    
                    apiService.trackPerformanceEvent(
                        eventType = eventType,
                        params = params,
                        appName = DeviceInfo.getAppName(),
                        deviceId = DeviceInfo.getDeviceId(),
                        os = DeviceInfo.getOs(),
                        language = DeviceInfo.getLanguage(),
                        sessionId = sessionId,
                        anonymousId = anonymousId
                    )
                } catch (e: Exception) {
                    Logger.e("An error occurred in performance event tracker thread", e)
                }
            }
        } catch (e: Exception) {
            Logger.e("Failed to submit performance event to executor", e)
        }
    }

    fun shutdown() {
        try {
            executor.shutdown()
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow()
                if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    Logger.e("Executor did not terminate")
                }
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
            Logger.e("Interrupted while shutting down executor", e)
        } catch (e: Exception) {
            Logger.e("An error occurred while shutting down executor", e)
        }
    }
}

