package com.mimeda.sdk.utils

import android.util.Log

/**
 * SDK için logging utility
 * Tüm loglar "MimedaSDK" tag'i ile yazılır
 * Sadece info, error ve success logları kullanılır
 * 
 * Güvenlik: Exception stack trace'leri production'da loglanmaz,
 * sadece exception mesajı loglanır. Detaylı stack trace'ler
 * sadece development ortamında gerekli olduğunda kullanılabilir.
 */
internal object Logger {
    private const val TAG = "MimedaSDK"
    internal val DEBUG = com.mimeda.sdk.BuildConfig.DEBUG_LOGGING

    fun i(message: String) {
        if (DEBUG) {
            Log.i(TAG, message)
        }
    }

    /**
     * Error logları - Güvenlik için stack trace loglanmaz
     * Sadece exception mesajı loglanır
     */
    fun e(message: String, throwable: Throwable? = null) {
        if (DEBUG) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
                val errorMessage = if (throwable.message != null) {
                    "$message: ${throwable.message}"
                } else {
                    "$message: ${throwable.javaClass.simpleName}"
                }
                Log.e(TAG, errorMessage)
            } else {
                Log.e(TAG, message)
            }
        }
    }

    fun s(message: String) {
        if (DEBUG) {
            Log.i(TAG, message)
        }
    }
}

