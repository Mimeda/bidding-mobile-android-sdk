package com.mimeda.sdk.events

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
internal class EventTracker(private val apiService: ApiService) {
    private val executor = Executors.newSingleThreadExecutor()
    private var sessionId: String? = null

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
                    // Session ID'yi ilk event'te oluştur ve sakla
                    if (sessionId == null) {
                        sessionId = System.currentTimeMillis().toString()
                    }
                    
                    apiService.trackEvent(
                        eventName = eventName,
                        eventParameter = eventParameter,
                        params = params,
                        eventType = eventType,
                        appName = DeviceInfo.getAppName(),
                        deviceId = DeviceInfo.getDeviceId(),
                        os = DeviceInfo.getOs(),
                        language = DeviceInfo.getLanguage(),
                        browser = DeviceInfo.getBrowser(),
                        sessionId = params.sessionId ?: sessionId,
                        traceId = params.traceId ?: java.util.UUID.randomUUID().toString()
                    )
                } catch (e: Exception) {
                    // Tüm exception'lar burada yakalanır
                    Logger.e("Error in event tracker thread", e)
                }
            }
        } catch (e: Exception) {
            // Executor exception'ı bile yakalanır
            Logger.e("Error submitting event to executor", e)
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
                    // Session ID'yi ilk event'te oluştur ve sakla
                    if (sessionId == null) {
                        sessionId = System.currentTimeMillis().toString()
                    }
                    
                    apiService.trackPerformanceEvent(
                        eventType = eventType,
                        params = params,
                        appName = DeviceInfo.getAppName(),
                        deviceId = DeviceInfo.getDeviceId(),
                        os = DeviceInfo.getOs(),
                        browser = DeviceInfo.getBrowser(),
                        sessionId = params.sessionId ?: sessionId,
                        traceId = params.traceId ?: java.util.UUID.randomUUID().toString()
                    )
                } catch (e: Exception) {
                    // Tüm exception'lar burada yakalanır
                    Logger.e("Error in performance event tracker thread", e)
                }
            }
        } catch (e: Exception) {
            // Executor exception'ı bile yakalanır
            Logger.e("Error submitting performance event to executor", e)
        }
    }

    /**
     * Executor'ı kapatır (cleanup için)
     */
    fun shutdown() {
        try {
            executor.shutdown()
        } catch (e: Exception) {
            Logger.e("Error shutting down executor", e)
        }
    }
}

