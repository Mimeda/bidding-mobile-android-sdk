package com.mimeda.sdk.events

import android.content.Context
import com.mimeda.sdk.api.ApiService
import com.mimeda.sdk.events.PerformanceEventParams
import com.mimeda.sdk.events.PerformanceEventType
import com.mimeda.sdk.utils.DeviceInfo
import com.mimeda.sdk.utils.Logger
import java.util.concurrent.Executors

/**
 * Event Tracker - Event'leri background thread'de API'ye gönderir
 * Tüm hatalar yakalanır ve ana uygulamaya yansıtılmaz
 */
internal class EventTracker(
    private val apiService: ApiService,
    private val context: Context
) {
    private val executor = Executors.newSingleThreadExecutor()
    
    companion object {
        private const val SESSION_DURATION_MS = 30 * 60 * 1000L // 30 dakika
        private const val PREFS_NAME = "mimeda_sdk_session"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_SESSION_TIMESTAMP = "session_timestamp"
    }

    /**
     * Session ID'yi SharedPreferences'ten alır veya yeni oluşturur
     * 30 dakika geçmişse yeni session oluşturulur
     * @return Session ID string
     */
    private fun getOrCreateSessionId(): String {
        return try {
            val currentTime = System.currentTimeMillis()
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedSessionId = prefs.getString(KEY_SESSION_ID, null)
            val savedTimestamp = prefs.getLong(KEY_SESSION_TIMESTAMP, 0L)
            
            // Session yoksa veya 30 dakika geçmişse yeni session oluştur
            if (savedSessionId == null || (currentTime - savedTimestamp) > SESSION_DURATION_MS) {

                val newSessionId = java.util.UUID.randomUUID().toString()
                prefs.edit()
                    .putString(KEY_SESSION_ID, newSessionId)
                    .putLong(KEY_SESSION_TIMESTAMP, currentTime)
                    .apply()
                newSessionId
            } else {
                savedSessionId
            }
        } catch (e: Exception) {
            // Hata durumunda fallback olarak memory'de sessionId oluştur
            Logger.e("Failed to get or create session ID from SharedPreferences", e)
            java.util.UUID.randomUUID().toString()
        }
    }

    /**
     * Event'i background thread'de API'ye gönderir
     * @param eventName Event adı
     * @param eventParameter Event parametresi
     * @param params Event parametreleri
     * @param eventType Event tipi (EVENT veya PERFORMANCE)
     */
    fun track(
        eventName: EventName,
        eventParameter: EventParameter,
        params: EventParams,
        eventType: EventType
    ) {
        try {
            executor.execute {
                try {
                    // Session ID'yi SharedPreferences'ten al veya oluştur
                    val sessionId = getOrCreateSessionId()
                    
                    apiService.trackEvent(
                        eventName = eventName,
                        eventParameter = eventParameter,
                        params = params,
                        eventType = eventType,
                        appName = DeviceInfo.getAppName(),
                        deviceId = DeviceInfo.getDeviceId(),
                        os = DeviceInfo.getOs(),
                        language = DeviceInfo.getLanguage(),
                        sessionId = sessionId
                    )
                } catch (e: Exception) {
                    // Tüm exception'lar burada yakalanır
                    Logger.e("An error occurred in event tracker thread", e)
                }
            }
        } catch (e: Exception) {
            // Executor exception'ı bile yakalanır
            Logger.e("Failed to submit event to executor", e)
        }
    }
    
    /**
     * Performance event'i background thread'de API'ye gönderir
     * @param eventType Performance event tipi (IMPRESSION veya CLICK)
     * @param params Performance event parametreleri
     */
    fun trackPerformance(
        eventType: PerformanceEventType,
        params: PerformanceEventParams
    ) {
        try {
            executor.execute {
                try {
                    // Session ID'yi SharedPreferences'ten al veya oluştur
                    val sessionId = getOrCreateSessionId()
                    
                    apiService.trackPerformanceEvent(
                        eventType = eventType,
                        params = params,
                        appName = DeviceInfo.getAppName(),
                        deviceId = DeviceInfo.getDeviceId(),
                        os = DeviceInfo.getOs(),
                        language = DeviceInfo.getLanguage(),
                        sessionId = sessionId
                    )
                } catch (e: Exception) {
                    // Tüm exception'lar burada yakalanır
                    Logger.e("An error occurred in performance event tracker thread", e)
                }
            }
        } catch (e: Exception) {
            // Executor exception'ı bile yakalanır
            Logger.e("Failed to submit performance event to executor", e)
        }
    }

    /**
     * Executor'ı kapatır (cleanup için)
     */
    fun shutdown() {
        try {
            executor.shutdown()
        } catch (e: Exception) {
            Logger.e("An error occurred while shutting down executor", e)
        }
    }
}

