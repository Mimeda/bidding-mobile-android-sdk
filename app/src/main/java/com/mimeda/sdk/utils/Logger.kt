package com.mimeda.sdk.utils

import android.util.Log

internal object Logger {
    private const val TAG = "MimedaSDK"
    
    // Production'da logging otomatik kapalı, sadece debug build'lerde açılabilir
    internal var isDebugEnabled = com.mimeda.sdk.BuildConfig.DEBUG_LOGGING && com.mimeda.sdk.BuildConfig.DEBUG
        private set
    
    /**
     * Runtime'da debug logging'i açıp kapatmak için kullanılır.
     * Sadece debug build'lerde çalışır, release build'lerde her zaman kapalıdır.
     */
    internal fun setDebugLogging(enabled: Boolean) {
        isDebugEnabled = enabled && com.mimeda.sdk.BuildConfig.DEBUG
    }

    fun i(message: String) {
        if (isDebugEnabled) {
            Log.i(TAG, message)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (isDebugEnabled) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
            } else {
                Log.e(TAG, message)
            }
        }
    }

    fun s(message: String) {
        if (isDebugEnabled) {
            Log.i(TAG, message)
        }
    }
}

