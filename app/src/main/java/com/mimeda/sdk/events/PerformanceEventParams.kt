package com.mimeda.sdk.events

/**
 * Performance event parametreleri - Query parametrelerini tutar
 * Impression ve Click event'leri için kullanılır
 */
data class PerformanceEventParams(
    /**
     * Line Item ID (li) - Kampanya ID'si - Zorunlu
     */
    val lineItemId: String,
    
    /**
     * Creative ID (c) - Line item ve SKU ikilisi için creative ID - Zorunlu
     */
    val creativeId: String,
    
    /**
     * Ad Unit (au) - Hangi ad unitte bulunduğu - Zorunlu
     */
    val adUnit: String,
    
    /**
     * Product SKU (psku) - Ürün SKU numarası - Zorunlu
     */
    val productSku: String,
    
    /**
     * Payload (pyl) - Second seat price payload - Zorunlu
     */
    val payload: String,
    
    /**
     * Keyword (kw) - Arama kelimesi - Opsiyonel
     */
    val keyword: String? = null,
    
    /**
     * Anonymous ID (aid) - Opsiyonel (otomatik doldurulur)
     */
    val anonymousId: String? = null,
    
    /**
     * User ID (uid) - Opsiyonel (boş olabilir)
     */
    val userId: String? = null,
    
    /**
     * Timestamp (t) - Opsiyonel (otomatik doldurulur)
     */
    val timestamp: Long? = null,
    
    /**
     * Session ID (s) - Opsiyonel (otomatik doldurulur)
     */
    val sessionId: String? = null,
    
    /**
     * Trace ID (tid) - Opsiyonel (otomatik doldurulur)
     */
    val traceId: String? = null
)

