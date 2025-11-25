package com.mimeda.sdk.utils

import android.content.Context
import android.provider.Settings
import java.util.Locale
import java.util.UUID

/**
 * Cihaz bilgilerini toplar ve yönetir
 */
internal object DeviceInfo {
    private var deviceId: String? = null
    private var appName: String? = null
    
    /**
     * Cihaz bilgilerini initialize eder
     * @param context Android Context
     */
    fun initialize(context: Context) {
        try {
            // Device ID - Android ID kullan, yoksa UUID oluştur
            deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: UUID.randomUUID().toString()
            
            // App name - Package name kullan
            appName = context.packageName
        } catch (e: Exception) {
            Logger.e("Error initializing DeviceInfo", e)
            // Fallback değerler
            deviceId = UUID.randomUUID().toString()
            appName = "unknown"
        }
    }
    
    /**
     * Device ID'yi döndürür
     */
    fun getDeviceId(): String {
        return deviceId ?: UUID.randomUUID().toString()
    }
    
    /**
     * App name'i döndürür
     */
    fun getAppName(): String {
        return appName ?: "unknown"
    }
    
    /**
     * İşletim sistemi bilgisini döndürür
     */
    fun getOs(): String {
        return "Android"
    }
    
    /**
     * Dil bilgisini döndürür (örn: tr-TR)
     */
    fun getLanguage(): String {
        val locale = Locale.getDefault()
        return "${locale.language}-${locale.country}"
    }
    
    /**
     * Browser bilgisini döndürür (Android'de genellikle boş)
     */
    fun getBrowser(): String? {
        return null
    }
}

