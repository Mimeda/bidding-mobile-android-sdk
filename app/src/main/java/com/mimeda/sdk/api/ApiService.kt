package com.mimeda.sdk.api

import com.mimeda.sdk.Environment
import com.mimeda.sdk.MimedaSDKErrorCallback
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
import java.net.SocketTimeoutException
import java.util.UUID

internal class ApiService(
    private val client: okhttp3.OkHttpClient,
    private val environment: Environment,
    private val errorCallback: MimedaSDKErrorCallback? = null,
    private val testEventBaseUrl: String? = null,
    private val testPerformanceBaseUrl: String? = null
) {
    private val sdkVersion = com.mimeda.sdk.BuildConfig.SDK_VERSION
    private val maxRetries = com.mimeda.sdk.BuildConfig.MAX_RETRIES
    private val retryBaseDelayMs = com.mimeda.sdk.BuildConfig.RETRY_BASE_DELAY_MS
    
    private val eventBaseUrl: String = testEventBaseUrl ?: when (environment) {
        Environment.PRODUCTION -> com.mimeda.sdk.BuildConfig.PRODUCTION_EVENT_BASE_URL
        Environment.STAGING -> com.mimeda.sdk.BuildConfig.STAGING_EVENT_BASE_URL
    }
    
    private val performanceBaseUrl: String = testPerformanceBaseUrl ?: when (environment) {
        Environment.PRODUCTION -> com.mimeda.sdk.BuildConfig.PRODUCTION_PERFORMANCE_BASE_URL
        Environment.STAGING -> com.mimeda.sdk.BuildConfig.STAGING_PERFORMANCE_BASE_URL
    }
    
    private fun getBaseUrl(eventType: EventType): String {
        return when (eventType) {
            EventType.EVENT -> eventBaseUrl
            EventType.PERFORMANCE -> performanceBaseUrl
        }
    }
    
    private fun buildQueryParams(
        eventName: EventName,
        eventParameter: EventParameter,
        params: EventParams,
        appName: String,
        deviceId: String,
        os: String,
        language: String,
        sessionId: String?,
        anonymousId: String?
    ): Map<String, String> {
        val queryParams = mutableMapOf<String, String>()
        val traceId = UUID.randomUUID().toString()
        
        queryParams["v"] = sdkVersion
        queryParams["app"] = appName
        queryParams["t"] = System.currentTimeMillis().toString()
        queryParams["d"] = deviceId
        queryParams["os"] = os
        queryParams["lng"] = language
        queryParams["en"] = eventName.value
        queryParams["ep"] = eventParameter.value
        queryParams["tid"] = traceId
        
        anonymousId?.let { queryParams["aid"] = it }
        params.userId?.let { queryParams["uid"] = it }
        params.lineItemIds?.let { queryParams["li"] = it }
        params.productList?.let { queryParams["pl"] = it }
        sessionId?.let { queryParams["s"] = it }
        params.categoryId?.let { queryParams["ct"] = it }
        params.keyword?.let { queryParams["kw"] = it }
        params.loyaltyCard?.let { queryParams["lc"] = it }
        params.transactionId?.let { queryParams["trans"] = it }
        params.totalRowCount?.let { queryParams["trc"] = it.toString() }
        
        return queryParams
    }
    
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

    private fun executeWithRetry(request: Request, eventName: String = ""): Boolean {
        var retryCount = 0
        
        while (retryCount <= maxRetries) {
            try {
                val response = client.newCall(request).execute()
                val isSuccess = response.isSuccessful
                
                if (isSuccess) {
                    if (eventName.isNotBlank()) {
                        Logger.s("Event tracked successfully. Event: $eventName, Status: ${response.code}")
                    }
                    response.close()
                    return true
                } else {
                    response.close()
                    val statusCode = response.code
                    
                    if (statusCode in 400..499) {
                        if (eventName.isNotBlank()) {
                            Logger.e("Event tracking failed. Event: $eventName, Status: $statusCode, Message: ${response.message}")
                        }
                        return false
                    }
                    
                    if (retryCount == maxRetries) {
                        if (eventName.isNotBlank()) {
                            Logger.e("Event tracking failed after retries. Event: $eventName, Status: $statusCode")
                        }
                        return false
                    }
                }
            } catch (e: SocketTimeoutException) {
                if (retryCount == maxRetries) {
                    if (eventName.isNotBlank()) {
                        Logger.e("Network timeout after retries. Event: $eventName", e)
                    }
                    return false
                }
            } catch (e: IOException) {
                if (retryCount == maxRetries) {
                    if (eventName.isNotBlank()) {
                        Logger.e("Network error after retries. Event: $eventName", e)
                    }
                    return false
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                return false
            } catch (e: Exception) {
                if (eventName.isNotBlank()) {
                    Logger.e("Unexpected error. Event: $eventName", e)
                }
                return false
            }
            
            retryCount++
            if (retryCount <= maxRetries) {
                try {
                    val delay = retryBaseDelayMs * (1 shl (retryCount - 1))
                    Thread.sleep(delay)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return false
                }
            }
        }
        
        return false
    }

    fun trackEvent(
        eventName: EventName,
        eventParameter: EventParameter,
        params: EventParams,
        eventType: EventType,
        appName: String,
        deviceId: String,
        os: String,
        language: String,
        sessionId: String?,
        anonymousId: String?
    ): Boolean {
        return try {
            val baseUrl = getBaseUrl(eventType)
            val queryParams = buildQueryParams(
                eventName, eventParameter, params,
                appName, deviceId, os, language, sessionId, anonymousId
            )
            val url = buildUrl(baseUrl, queryParams)

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val success = executeWithRetry(request, "${eventName.value}/${eventParameter.value}")
            if (!success) {
                try {
                    errorCallback?.onEventTrackingFailed(eventName, eventParameter, Exception("Event tracking failed"))
                } catch (callbackException: Exception) {
                    Logger.e("Error in event tracking callback", callbackException)
                }
            }
            return success
        } catch (e: Exception) {
            Logger.e("An unexpected error occurred while tracking event: ${eventName.value}/${eventParameter.value}", e)
            try {
                errorCallback?.onEventTrackingFailed(eventName, eventParameter, e)
            } catch (callbackException: Exception) {
                Logger.e("Error in event tracking callback", callbackException)
            }
            false
        }
    }
    
    private fun buildPerformanceQueryParams(
        params: PerformanceEventParams,
        appName: String,
        deviceId: String,
        os: String,
        language: String,
        sessionId: String?,
        anonymousId: String?
    ): Map<String, String> {
        val queryParams = mutableMapOf<String, String>()
        val traceId = UUID.randomUUID().toString()
        
        queryParams["v"] = sdkVersion
        params.lineItemId?.let { queryParams["li"] = it }
        params.creativeId?.let { queryParams["c"] = it }
        params.adUnit?.let { queryParams["au"] = it }
        params.productSku?.let { queryParams["psku"] = it }
        params.payload?.let { queryParams["pyl"] = it }
        queryParams["t"] = System.currentTimeMillis().toString()
        queryParams["os"] = os
        queryParams["app"] = appName
        queryParams["d"] = deviceId
        queryParams["lng"] = language
        queryParams["tid"] = traceId
        
        params.keyword?.let { queryParams["kw"] = it }
        anonymousId?.let { queryParams["aid"] = it }
        params.userId?.let { queryParams["uid"] = it }
        sessionId?.let { queryParams["s"] = it }
        
        return queryParams
    }
    
    private fun getPerformanceEndpoint(eventType: PerformanceEventType): String {
        return when (eventType) {
            PerformanceEventType.IMPRESSION -> "impressions"
            PerformanceEventType.CLICK -> "clicks"
        }
    }
    
    fun trackPerformanceEvent(
        eventType: PerformanceEventType,
        params: PerformanceEventParams,
        appName: String,
        deviceId: String,
        os: String,
        language: String,
        sessionId: String?,
        anonymousId: String?
    ): Boolean {
        return try {
            val endpoint = getPerformanceEndpoint(eventType)
            val queryParams = buildPerformanceQueryParams(
                params, appName, deviceId, os, language, sessionId, anonymousId
            )
            
            val httpUrl = "$performanceBaseUrl/$endpoint".toHttpUrl()
            val urlBuilder = httpUrl.newBuilder()
            
            queryParams.forEach { (key, value) ->
                if (value.isNotBlank()) {
                    urlBuilder.addQueryParameter(key, value)
                }
            }
            
            val url = urlBuilder.build().toString()

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val success = executeWithRetry(request, "Performance/$eventType")
            if (!success) {
                try {
                    errorCallback?.onPerformanceEventTrackingFailed(eventType, Exception("Performance event tracking failed"))
                } catch (callbackException: Exception) {
                    Logger.e("Error in performance event tracking callback", callbackException)
                }
            }
            return success
        } catch (e: Exception) {
            Logger.e("An unexpected error occurred while tracking performance event: $eventType", e)
            try {
                errorCallback?.onPerformanceEventTrackingFailed(eventType, e)
            } catch (callbackException: Exception) {
                Logger.e("Error in performance event tracking callback", callbackException)
            }
            false
        }
    }
}
