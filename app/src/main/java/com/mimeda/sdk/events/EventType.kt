package com.mimeda.sdk.events

/**
 * Event tipi - Hangi base URL'ye gönderileceğini belirler
 */
internal enum class EventType {
    /**
     * Normal event'ler için - event.mlink.com.tr'ye gönderilir
     */
    EVENT,
    
    /**
     * Performans event'leri için - performance.mlink.com.tr'ye gönderilir
     */
    PERFORMANCE
}

