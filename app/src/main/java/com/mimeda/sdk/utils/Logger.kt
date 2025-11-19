package com.mimeda.sdk.utils

import android.util.Log

/**
 * SDK için logging utility
 * Tüm loglar "MimedaSDK" tag'i ile yazılır
 */
internal object Logger {
    private const val TAG = "MimedaSDK"
    internal val DEBUG = com.mimeda.sdk.BuildConfig.DEBUG_LOGGING

    fun d(message: String) {
        if (DEBUG) {
            Log.d(TAG, message)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (DEBUG) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
            } else {
                Log.e(TAG, message)
            }
        }
    }

    fun i(message: String) {
        if (DEBUG) {
            Log.i(TAG, message)
        }
    }
}

