package com.mimeda.sdk.events

import android.content.Context
import com.mimeda.sdk.api.ApiService
import com.mimeda.sdk.utils.DeviceInfo
import com.mimeda.sdk.utils.InputValidator
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
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_SESSION_TIMESTAMP = "session_timestamp"
        private const val KEY_ANONYMOUS_ID = "anonymous_id"
        private const val SHUTDOWN_TIMEOUT_SECONDS = 5L
    }

    private fun getOrCreateSessionId(): String {
        return try {
            val currentTime = System.currentTimeMillis()
            val savedSessionId = SecurePreferences.getString(context, KEY_SESSION_ID)
            val savedTimestamp = SecurePreferences.getLong(context, KEY_SESSION_TIMESTAMP, 0L)
            
            if (savedSessionId == null || (currentTime - savedTimestamp) > SESSION_DURATION_MS) {
                val newSessionId = java.util.UUID.randomUUID().toString()
                SecurePreferences.putString(context, KEY_SESSION_ID, newSessionId)
                SecurePreferences.putLong(context, KEY_SESSION_TIMESTAMP, currentTime)
                newSessionId
            } else {
                savedSessionId
            }
        } catch (e: Exception) {
            Logger.e("Failed to get or create session ID from SecurePreferences", e)
            java.util.UUID.randomUUID().toString()
        }
    }

    private fun getOrCreateAnonymousId(): String {
        return try {
            val savedAnonymousId = SecurePreferences.getString(context, KEY_ANONYMOUS_ID)
            
            if (savedAnonymousId == null) {
                val newAnonymousId = java.util.UUID.randomUUID().toString()
                SecurePreferences.putString(context, KEY_ANONYMOUS_ID, newAnonymousId)
                newAnonymousId
            } else {
                savedAnonymousId
            }
        } catch (e: Exception) {
            Logger.e("Failed to get or create anonymous ID from SecurePreferences", e)
            java.util.UUID.randomUUID().toString()
        }
    }

    private fun sanitizeParams(params: EventParams): EventParams {
        return EventParams(
            userId = InputValidator.sanitizeUserId(params.userId),
            lineItemIds = InputValidator.sanitizeString(params.lineItemIds),
            productList = InputValidator.sanitizeProductList(params.productList),
            categoryId = InputValidator.sanitizeString(params.categoryId),
            keyword = InputValidator.sanitizeKeyword(params.keyword),
            loyaltyCard = InputValidator.sanitizeString(params.loyaltyCard),
            transactionId = InputValidator.sanitizeString(params.transactionId),
            totalRowCount = params.totalRowCount,
            app = InputValidator.sanitizeApp(params.app)
        )
    }

    private fun sanitizePerformanceParams(params: PerformanceEventParams): PerformanceEventParams {
        return PerformanceEventParams(
            lineItemId = InputValidator.sanitizeString(params.lineItemId),
            creativeId = InputValidator.sanitizeString(params.creativeId),
            adUnit = InputValidator.sanitizeString(params.adUnit),
            productSku = InputValidator.sanitizeString(params.productSku),
            payload = InputValidator.sanitizePayload(params.payload),
            keyword = InputValidator.sanitizeKeyword(params.keyword),
            userId = InputValidator.sanitizeUserId(params.userId),
            app = InputValidator.sanitizeApp(params.app)
        )
    }

    fun track(
        eventName: EventName,
        eventParameter: EventParameter,
        params: EventParams,
        eventType: EventType
    ) {
        try {
            val sanitizedParams = sanitizeParams(params)
            
            executor.execute {
                try {
                    val sessionId = getOrCreateSessionId()
                    val anonymousId = getOrCreateAnonymousId()
                    
                    apiService.trackEvent(
                        eventName = eventName,
                        eventParameter = eventParameter,
                        params = sanitizedParams,
                        eventType = eventType,
                        appName = sanitizedParams.app ?: DeviceInfo.getAppName(),
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
            val sanitizedParams = sanitizePerformanceParams(params)
            
            executor.execute {
                try {
                    val sessionId = getOrCreateSessionId()
                    val anonymousId = getOrCreateAnonymousId()
                    
                    apiService.trackPerformanceEvent(
                        eventType = eventType,
                        params = sanitizedParams,
                        appName = sanitizedParams.app ?: DeviceInfo.getAppName(),
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
