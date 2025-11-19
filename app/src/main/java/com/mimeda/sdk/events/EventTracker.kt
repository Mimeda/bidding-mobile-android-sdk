package com.mimeda.sdk.events

import com.mimeda.sdk.api.ApiService
import com.mimeda.sdk.utils.Logger
import java.util.concurrent.Executors

/**
 * Event Tracker - Event'leri background thread'de API'ye gönderir
 * Tüm hatalar yakalanır ve ana uygulamaya yansıtılmaz
 */
internal class EventTracker(private val apiService: ApiService) {
    private val executor = Executors.newSingleThreadExecutor()

    /**
     * Event'i background thread'de API'ye gönderir
     * @param eventName Event adı
     * @param params Event parametreleri
     */
    fun track(eventName: String, params: Map<String, Any>) {
        try {
            executor.execute {
                try {
                    apiService.trackEvent(eventName, params)
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

