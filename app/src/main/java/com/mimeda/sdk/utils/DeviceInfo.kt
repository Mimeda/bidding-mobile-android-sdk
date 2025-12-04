package com.mimeda.sdk.utils

import android.content.Context
import android.provider.Settings
import java.util.Locale
import java.util.UUID

internal object DeviceInfo {
    @Volatile
    private var deviceId: String? = null
    
    @Volatile
    private var appName: String? = null
    
    @Synchronized
    fun initialize(context: Context) {
        try {
            deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: UUID.randomUUID().toString()
            
            appName = context.packageName
        } catch (e: Exception) {
            Logger.e("Failed to initialize DeviceInfo", e)
            deviceId = UUID.randomUUID().toString()
            appName = "unknown"
        }
    }
    
    fun getDeviceId(): String {
        return deviceId ?: UUID.randomUUID().toString()
    }
    
    fun getAppName(): String {
        return appName ?: "unknown"
    }
    
    fun getOs(): String {
        return "Android"
    }
    
    fun getLanguage(): String {
        val locale = Locale.getDefault()
        return "${locale.language}-${locale.country}"
    }
    
    fun getBrowser(): String? {
        return null
    }
}

