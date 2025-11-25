package com.mimeda.sdk.events

/**
 * Event parametreleri - Query parametrelerini tutar
 * Tüm parametreler opsiyoneldir, sadece gerekli olanlar set edilir
 */
data class EventParams(
    /**
     * SDK versiyonu (v)
     */
    val sdkVersion: String? = null,
    
    /**
     * Uygulama adı (app)
     */
    val app: String? = null,
    
    /**
     * Timestamp (t)
     */
    val timestamp: Long? = null,
    
    /**
     * Device ID (d)
     */
    val deviceId: String? = null,
    
    /**
     * İşletim sistemi (os)
     */
    val os: String? = null,
    
    /**
     * Browser (br) - Android'de genellikle boş
     */
    val browser: String? = null,
    
    /**
     * Dil (lng)
     */
    val language: String? = null,
    
    /**
     * Anonymous ID (aid)
     */
    val anonymousId: String? = null,
    
    /**
     * User ID (uid)
     */
    val userId: String? = null,
    
    /**
     * Line Item IDs (li)
     */
    val lineItemIds: String? = null,
    
    /**
     * Product List (pl) - Ürün listesi (sku, adet, fiyat)
     */
    val productList: String? = null,
    
    /**
     * Session ID (s)
     */
    val sessionId: String? = null,
    
    /**
     * Category ID (ct)
     */
    val categoryId: String? = null,
    
    /**
     * Keyword (kw) - Arama kelimesi
     */
    val keyword: String? = null,
    
    /**
     * Loyalty Card (lc)
     */
    val loyaltyCard: String? = null,
    
    /**
     * Transaction ID (trans) - Sadece Purchase için
     */
    val transactionId: String? = null,
    
    /**
     * Total Row Count (trc)
     */
    val totalRowCount: Int? = null,
    
    /**
     * Trace ID (tid) - Unique trace identifier
     */
    val traceId: String? = null
)

