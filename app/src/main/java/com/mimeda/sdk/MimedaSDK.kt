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
import com.mimeda.sdk.events.PerformanceEventType
import com.mimeda.sdk.utils.DeviceInfo
import com.mimeda.sdk.utils.Logger

object MimedaSDK {
    @Volatile
    private var isInitialized = false
    
    @Volatile
    private var eventTracker: EventTracker? = null
    
    @Volatile
    private var errorCallback: MimedaSDKErrorCallback? = null

    @JvmOverloads
    @Synchronized
    fun initialize(
        context: Context,
        apiKey: String,
        environment: Environment = Environment.PRODUCTION,
        errorCallback: MimedaSDKErrorCallback? = null
    ) {
        try {
            if (isInitialized) {
                return
            }

            if (apiKey.isBlank()) {
                Logger.e("API key is required but was not provided")
                return
            }

            val appPackageName = context.packageName
            if (appPackageName.isBlank()) {
                Logger.e("Package name is required but could not be retrieved from context")
                return
            }

            DeviceInfo.initialize(context)

            this.errorCallback = errorCallback

            val client = ApiClient.createClient(apiKey, appPackageName)
            val apiService = ApiService(client, environment, errorCallback)
            val applicationContext = context.applicationContext
            eventTracker = EventTracker(apiService, applicationContext)

            isInitialized = true
            Logger.s("MimedaSDK initialized successfully. Package: $appPackageName, Environment: $environment")
        } catch (e: Exception) {
            Logger.e("Failed to initialize MimedaSDK", e)
        }
    }

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
        }
    }

    fun trackPerformanceImpression(params: PerformanceEventParams) {
        try {
            if (!isInitialized) {
                Logger.e("SDK is not initialized. Call initialize() before tracking events")
                return
            }

            eventTracker?.trackPerformance(
                PerformanceEventType.IMPRESSION,
                params
            ) ?: run {
                Logger.e("EventTracker is not available")
            }
        } catch (e: Exception) {
            Logger.e("An error occurred while tracking performance impression event", e)
        }
    }

    fun trackPerformanceClick(params: PerformanceEventParams) {
        try {
            if (!isInitialized) {
                Logger.e("SDK is not initialized. Call initialize() before tracking events")
                return
            }

            eventTracker?.trackPerformance(
                PerformanceEventType.CLICK,
                params
            ) ?: run {
                Logger.e("EventTracker is not available")
            }
        } catch (e: Exception) {
            Logger.e("An error occurred while tracking performance click event", e)
        }
    }

    fun isInitialized(): Boolean {
        return isInitialized
    }

    @Synchronized
    fun shutdown() {
        try {
            eventTracker?.shutdown()
            eventTracker = null
            isInitialized = false
        } catch (e: Exception) {
            Logger.e("An error occurred while shutting down MimedaSDK", e)
        }
    }
}

