package com.mimeda.sdk.utils

/**
 * Timestamp generator
 * Request timestamp'leri için kullanılır
 */
internal object TimestampGenerator {
    /**
     * Timestamp oluşturur (milliseconds)
     */
    fun getTimestamp(): Long {
        return System.currentTimeMillis()
    }
}

