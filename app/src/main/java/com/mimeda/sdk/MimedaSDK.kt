package com.mimeda.sdk

import android.content.Context
import com.mimeda.sdk.api.ApiClient
import com.mimeda.sdk.api.ApiService
import com.mimeda.sdk.events.EventName
import com.mimeda.sdk.events.EventParameter
import com.mimeda.sdk.events.EventParams
import com.mimeda.sdk.events.EventTracker
import com.mimeda.sdk.events.EventType
import com.mimeda.sdk.events.PerformanceEventParams
import com.mimeda.sdk.utils.DeviceInfo
import com.mimeda.sdk.utils.Logger

/**
 * Mimeda SDK - Ana SDK sınıfı
 * Singleton pattern kullanır
 * 
 * Kullanım:
 * ```
 * MimedaSDK.initialize(context, "your-api-key", Environment.PRODUCTION)
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
     * @param environment Environment seçimi (PRODUCTION veya STAGING), varsayılan: PRODUCTION
     * 
     * Package name otomatik olarak context.packageName'den alınır.
     * Geliştirici tarafından verilmez.
     */
    @JvmOverloads
    fun initialize(
        context: Context,
        apiKey: String,
        environment: Environment = Environment.PRODUCTION
    ) {
        try {
            if (isInitialized) {
                return
            }

            if (apiKey.isBlank()) {
                Logger.e("API key is required but was not provided")
                return
            }

            // Package name'i otomatik olarak context'ten al
            val appPackageName = context.packageName
            if (appPackageName.isBlank()) {
                Logger.e("Package name is required but could not be retrieved from context")
                return
            }

            // DeviceInfo'yu initialize et
            DeviceInfo.initialize(context)

            val client = ApiClient.createClient(apiKey, appPackageName)
            val apiService = ApiService(client, environment)
            // ApplicationContext kullanarak memory leak önlenir
            val applicationContext = context.applicationContext
            eventTracker = EventTracker(apiService, applicationContext)

            isInitialized = true
            Logger.s("MimedaSDK initialized successfully. Package: $appPackageName, Environment: $environment")
        } catch (e: Exception) {
            Logger.e("Failed to initialize MimedaSDK", e)
            // Exception ana uygulamaya yansıtılmaz
        }
    }

    /**
     * Event tracking yapar (event.mlink.com.tr'ye gönderilir)
     * @param eventName Event adı (enum)
     * @param eventParameter Event parametresi (enum)
     * @param params Event parametreleri (opsiyonel)
     */
    fun trackEvent(
        eventName: EventName,
        eventParameter: EventParameter,
        params: EventParams = EventParams()
    ) {
        try {
            if (!isInitialized) {
                Logger.e("SDK is not initialized. Call initialize() before tracking events")
                return
            }

            eventTracker?.track(eventName, eventParameter, params, EventType.EVENT) ?: run {
                Logger.e("EventTracker is not available")
            }
        } catch (e: Exception) {
            Logger.e("An error occurred while tracking event", e)
            // Exception ana uygulamaya yansıtılmaz
        }
    }

    /**
     * Performance impression event tracking yapar (performance.mlink.com.tr/impressions'ye gönderilir)
     * Search sonucu ekrana gelen ve kullanıcının ekranına girmiş ürün başına bir impression eventi tetiklenir
     * @param params Performance event parametreleri
     */
    fun trackPerformanceImpression(params: PerformanceEventParams) {
        try {
            if (!isInitialized) {
                Logger.e("SDK is not initialized. Call initialize() before tracking events")
                return
            }

            eventTracker?.trackPerformance(
                com.mimeda.sdk.events.PerformanceEventType.IMPRESSION,
                params
            ) ?: run {
                Logger.e("EventTracker is not available")
            }
        } catch (e: Exception) {
            Logger.e("An error occurred while tracking performance impression event", e)
            // Exception ana uygulamaya yansıtılmaz
        }
    }

    /**
     * Performance click event tracking yapar (performance.mlink.com.tr/clicks'ye gönderilir)
     * Sponsorlu çerçevesine sahip ürünlere tıklandığında tetiklenen eventtir
     * @param params Performance event parametreleri
     */
    fun trackPerformanceClick(params: PerformanceEventParams) {
        try {
            if (!isInitialized) {
                Logger.e("SDK is not initialized. Call initialize() before tracking events")
                return
            }

            eventTracker?.trackPerformance(
                com.mimeda.sdk.events.PerformanceEventType.CLICK,
                params
            ) ?: run {
                Logger.e("EventTracker is not available")
            }
        } catch (e: Exception) {
            Logger.e("An error occurred while tracking performance click event", e)
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
        } catch (e: Exception) {
            Logger.e("An error occurred while shutting down MimedaSDK", e)
            // Exception ana uygulamaya yansıtılmaz
        }
    }
}

