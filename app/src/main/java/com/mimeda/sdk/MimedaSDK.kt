package com.mimeda.sdk

import android.content.Context
import com.mimeda.sdk.api.ApiClient
import com.mimeda.sdk.api.ApiService
import com.mimeda.sdk.events.EventTracker
import com.mimeda.sdk.utils.Logger

/**
 * Mimeda SDK - Ana SDK sınıfı
 * Singleton pattern kullanır
 * 
 * Kullanım:
 * ```
 * MimedaSDK.initialize(context, "your-api-key")
 * MimedaSDK.trackEvent("button_clicked", mapOf("button_id" to "login"))
 * ```
 * 
 * Not: Package name otomatik olarak context'ten alınır, geliştirici tarafından verilmez.
 */
object MimedaSDK {
    private var isInitialized = false
    private var eventTracker: EventTracker? = null

    /**
     * SDK'yı başlatır
     * @param context Android Context
     * @param apiKey API anahtarı
     * 
     * Package name otomatik olarak context.packageName'den alınır.
     * Geliştirici tarafından verilmez.
     */
    fun initialize(context: Context, apiKey: String) {
        try {
            if (isInitialized) {
                Logger.d("SDK already initialized")
                return
            }

            if (apiKey.isBlank()) {
                Logger.e("API key cannot be blank")
                return
            }

            // Package name'i otomatik olarak context'ten al
            val appPackageName = context.packageName
            if (appPackageName.isBlank()) {
                Logger.e("Package name cannot be blank")
                return
            }

            val client = ApiClient.createClient(apiKey, appPackageName)
            val apiService = ApiService(client)
            eventTracker = EventTracker(apiService)

            isInitialized = true
            Logger.d("MimedaSDK initialized successfully with package: $appPackageName")
        } catch (e: Exception) {
            Logger.e("Error initializing MimedaSDK", e)
            // Exception ana uygulamaya yansıtılmaz
        }
    }

    /**
     * Event tracking yapar
     * @param eventName Event adı
     * @param params Event parametreleri (opsiyonel)
     */
    fun trackEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        try {
            if (!isInitialized) {
                Logger.e("SDK not initialized. Call initialize() first.")
                return
            }

            if (eventName.isBlank()) {
                Logger.e("Event name cannot be blank")
                return
            }

            eventTracker?.track(eventName, params) ?: run {
                Logger.e("EventTracker is null")
            }
        } catch (e: Exception) {
            Logger.e("Error in trackEvent", e)
            // Exception ana uygulamaya yansıtılmaz
        }
    }

    /**
     * SDK'nın başlatılıp başlatılmadığını kontrol eder
     */
    fun isInitialized(): Boolean {
        return isInitialized
    }

    /**
     * SDK'yı kapatır ve kaynakları temizler
     * ExecutorService'i düzgün şekilde kapatır (memory leak önleme)
     * 
     * Not: Genellikle uygulama kapanırken otomatik olarak temizlenir,
     * ancak test senaryolarında veya özel durumlarda manuel olarak çağrılabilir.
     */
    fun shutdown() {
        try {
            eventTracker?.shutdown()
            eventTracker = null
            isInitialized = false
            Logger.d("MimedaSDK shutdown completed")
        } catch (e: Exception) {
            Logger.e("Error shutting down MimedaSDK", e)
            // Exception ana uygulamaya yansıtılmaz
        }
    }
}

