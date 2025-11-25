package com.mimeda.sdk.api

import com.mimeda.sdk.events.EventName
import com.mimeda.sdk.events.EventParameter
import com.mimeda.sdk.events.EventParams
import com.mimeda.sdk.events.EventType
import com.mimeda.sdk.events.PerformanceEventParams
import com.mimeda.sdk.events.PerformanceEventType
import com.mimeda.sdk.utils.Logger
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import java.io.IOException
import java.util.UUID

/**
 * API Service - HTTP isteklerini yönetir
 * Event tracking için API endpoint'lerine query parametreli GET isteği gönderir
 */
internal class ApiService(private val client: okhttp3.OkHttpClient) {
    private val eventBaseUrl = com.mimeda.sdk.BuildConfig.EVENT_BASE_URL
    private val performanceBaseUrl = com.mimeda.sdk.BuildConfig.PERFORMANCE_BASE_URL
    private val sdkVersion = com.mimeda.sdk.BuildConfig.SDK_VERSION
    
    /**
     * Event tipine göre doğru base URL'yi döndürür
     */
    private fun getBaseUrl(eventType: EventType): String {
        return when (eventType) {
            EventType.EVENT -> eventBaseUrl
            EventType.PERFORMANCE -> performanceBaseUrl
        }
    }
    
    /**
     * EventParams'i query parametrelerine dönüştürür
     */
    private fun buildQueryParams(
        eventName: EventName,
        eventParameter: EventParameter,
        params: EventParams,
        appName: String,
        deviceId: String,
        os: String,
        language: String,
        browser: String?,
        sessionId: String?,
        traceId: String
    ): Map<String, String> {
        val queryParams = mutableMapOf<String, String>()
        
        // Zorunlu parametreler
        queryParams["v"] = sdkVersion
        queryParams["app"] = params.app ?: appName
        queryParams["t"] = (params.timestamp ?: System.currentTimeMillis()).toString()
        queryParams["d"] = params.deviceId ?: deviceId
        queryParams["os"] = params.os ?: os
        queryParams["lng"] = params.language ?: language
        queryParams["en"] = eventName.value
        queryParams["ep"] = eventParameter.value
        queryParams["tid"] = params.traceId ?: traceId
        
        // Opsiyonel parametreler
        params.browser?.let { queryParams["br"] = it } ?: browser?.let { queryParams["br"] = it }
        params.anonymousId?.let { queryParams["aid"] = it }
        params.userId?.let { queryParams["uid"] = it }
        params.lineItemIds?.let { queryParams["li"] = it }
        params.productList?.let { queryParams["pl"] = it }
        params.sessionId?.let { queryParams["s"] = it } ?: sessionId?.let { queryParams["s"] = it }
        params.categoryId?.let { queryParams["ct"] = it }
        params.keyword?.let { queryParams["kw"] = it }
        params.loyaltyCard?.let { queryParams["lc"] = it }
        params.transactionId?.let { queryParams["trans"] = it }
        params.totalRowCount?.let { queryParams["trc"] = it.toString() }
        
        return queryParams
    }
    
    /**
     * Query parametrelerini URL'e ekler
     */
    private fun buildUrl(baseUrl: String, queryParams: Map<String, String>): String {
        val httpUrl = "$baseUrl/events".toHttpUrl()
        val urlBuilder = httpUrl.newBuilder()
        
        queryParams.forEach { (key, value) ->
            if (value.isNotBlank()) {
                urlBuilder.addQueryParameter(key, value)
            }
        }
        
        return urlBuilder.build().toString()
    }

    /**
     * Event tracking için API'ye query parametreli GET isteği gönderir
     * @param eventName Event adı
     * @param eventParameter Event parametresi
     * @param params Event parametreleri
     * @param eventType Event tipi (EVENT veya PERFORMANCE)
     * @param appName Uygulama adı
     * @param deviceId Cihaz ID
     * @param os İşletim sistemi
     * @param language Dil
     * @param browser Browser (opsiyonel)
     * @param sessionId Session ID (opsiyonel)
     * @param traceId Trace ID
     * @return Başarılı olursa true, aksi halde false (exception fırlatmaz)
     */
    fun trackEvent(
        eventName: EventName,
        eventParameter: EventParameter,
        params: EventParams,
        eventType: EventType,
        appName: String,
        deviceId: String,
        os: String,
        language: String,
        browser: String? = null,
        sessionId: String? = null,
        traceId: String = UUID.randomUUID().toString()
    ): Boolean {
        return try {
            val baseUrl = getBaseUrl(eventType)
            val queryParams = buildQueryParams(
                eventName, eventParameter, params,
                appName, deviceId, os, language, browser, sessionId, traceId
            )
            val url = buildUrl(baseUrl, queryParams)

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            // REQUEST LOG
            Logger.d("📤 REQUEST → URL: $url")
            Logger.d("📤 REQUEST → Method: ${request.method}")
            Logger.d("📤 REQUEST → Event: ${eventName.value} / ${eventParameter.value}")
            Logger.d("📤 REQUEST → Query Params: $queryParams")

            val response = client.newCall(request).execute()

            // RESPONSE LOG - Body'yi okumadan önce peek kullan (body tüketilmez)
            val responseBodyPreview = response.peekBody(1024).string() // İlk 1KB'ı oku
            Logger.d("📥 RESPONSE → Status: ${response.code} ${response.message}")
            Logger.d("📥 RESPONSE → Headers: ${response.headers}")
            Logger.d("📥 RESPONSE → Body: $responseBodyPreview")

            val isSuccess = response.isSuccessful
            if (isSuccess) {
                Logger.d("✅ Event tracked successfully: ${eventName.value}/${eventParameter.value} (Status: ${response.code})")
            } else {
                Logger.e("❌ Event tracking failed: ${eventName.value}/${eventParameter.value} (Status: ${response.code} - ${response.message})")
            }

            response.close()
            isSuccess
        } catch (e: IOException) {
            Logger.e("🌐 Network error while tracking event: ${eventName.value}/${eventParameter.value}", e)
            false
        } catch (e: Exception) {
            Logger.e("⚠️ Unexpected error while tracking event: ${eventName.value}/${eventParameter.value}", e)
            false
        }
    }
    
    /**
     * PerformanceEventParams'i query parametrelerine dönüştürür
     */
    private fun buildPerformanceQueryParams(
        params: PerformanceEventParams,
        appName: String,
        deviceId: String,
        os: String,
        browser: String?,
        sessionId: String?,
        traceId: String
    ): Map<String, String> {
        val queryParams = mutableMapOf<String, String>()
        
        // Zorunlu parametreler
        queryParams["li"] = params.lineItemId
        queryParams["c"] = params.creativeId
        queryParams["au"] = params.adUnit
        queryParams["psku"] = params.productSku
        queryParams["pyl"] = params.payload
        queryParams["t"] = (params.timestamp ?: System.currentTimeMillis()).toString()
        queryParams["os"] = os
        queryParams["app"] = appName
        queryParams["tid"] = params.traceId ?: traceId
        
        // Opsiyonel parametreler
        params.keyword?.let { queryParams["kw"] = it }

        params.anonymousId?.let { queryParams["aid"] = it } ?:  ""

        params.userId?.let { queryParams["uid"] = it } ?:  ""

        params.sessionId?.let { queryParams["s"] = it } ?: sessionId?.let { queryParams["s"] = it }

        browser?.let { queryParams["br"] = it } ?: ""
        
        return queryParams
    }
    
    /**
     * Performance event endpoint'ini döndürür
     */
    private fun getPerformanceEndpoint(eventType: PerformanceEventType): String {
        return when (eventType) {
            PerformanceEventType.IMPRESSION -> "impressions"
            PerformanceEventType.CLICK -> "clicks"
        }
    }
    
    /**
     * Performance event tracking için API'ye query parametreli GET isteği gönderir
     * @param eventType Performance event tipi (IMPRESSION veya CLICK)
     * @param params Performance event parametreleri
     * @param appName Uygulama adı
     * @param deviceId Cihaz ID
     * @param os İşletim sistemi
     * @param browser Browser (opsiyonel)
     * @param sessionId Session ID (opsiyonel)
     * @param traceId Trace ID
     * @return Başarılı olursa true, aksi halde false (exception fırlatmaz)
     */
    fun trackPerformanceEvent(
        eventType: PerformanceEventType,
        params: PerformanceEventParams,
        appName: String,
        deviceId: String,
        os: String,
        browser: String? = null,
        sessionId: String? = null,
        traceId: String = UUID.randomUUID().toString()
    ): Boolean {
        return try {
            val endpoint = getPerformanceEndpoint(eventType)
            val queryParams = buildPerformanceQueryParams(
                params, appName, deviceId, os, browser, sessionId, traceId
            )
            
            val httpUrl = "$performanceBaseUrl/$endpoint".toHttpUrl()
            val urlBuilder = httpUrl.newBuilder()
            
            queryParams.forEach { (key, value) ->
                // Boş string'leri de gönder (uid, aid, br gibi)
                urlBuilder.addQueryParameter(key, value)
            }
            
            val url = urlBuilder.build().toString()

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            // REQUEST LOG
            Logger.d("📤 PERFORMANCE REQUEST → URL: $url")
            Logger.d("📤 PERFORMANCE REQUEST → Method: ${request.method}")
            Logger.d("📤 PERFORMANCE REQUEST → Event Type: $eventType")
            Logger.d("📤 PERFORMANCE REQUEST → Query Params: $queryParams")

            val response = client.newCall(request).execute()

            // RESPONSE LOG - Body'yi okumadan önce peek kullan (body tüketilmez)
            val responseBodyPreview = response.peekBody(1024).string() // İlk 1KB'ı oku
            Logger.d("📥 PERFORMANCE RESPONSE → Status: ${response.code} ${response.message}")
            Logger.d("📥 PERFORMANCE RESPONSE → Headers: ${response.headers}")
            Logger.d("📥 PERFORMANCE RESPONSE → Body: $responseBodyPreview")

            val isSuccess = response.isSuccessful
            if (isSuccess) {
                Logger.d("✅ Performance event tracked successfully: $eventType (Status: ${response.code})")
            } else {
                Logger.e("❌ Performance event tracking failed: $eventType (Status: ${response.code} - ${response.message})")
            }

            response.close()
            isSuccess
        } catch (e: IOException) {
            Logger.e("🌐 Network error while tracking performance event: $eventType", e)
            false
        } catch (e: Exception) {
            Logger.e("⚠️ Unexpected error while tracking performance event: $eventType", e)
            false
        }
    }
}

