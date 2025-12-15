package com.mimeda.sdk.utils

/**
 * Input validation utility sınıfı.
 * XSS, injection ve diğer güvenlik açıklarına karşı koruma sağlar.
 */
internal object InputValidator {
    
    // Maximum field uzunlukları
    private const val MAX_USER_ID_LENGTH = 256
    private const val MAX_STRING_FIELD_LENGTH = 1024
    private const val MAX_PRODUCT_LIST_LENGTH = 10240 // Product list daha uzun olabilir
    private const val MAX_KEYWORD_LENGTH = 256
    private const val MAX_PAYLOAD_LENGTH = 65536 // 64KB
    
    // Tehlikeli karakter pattern'leri
    private val SCRIPT_PATTERN = Regex("<script[^>]*>.*?</script>", RegexOption.IGNORE_CASE)
    private val HTML_TAG_PATTERN = Regex("<[^>]+>")
    private val SQL_INJECTION_PATTERN = Regex("('|--|;|/\\*|\\*/|@@|@|char|nchar|varchar|nvarchar|alter|begin|cast|create|cursor|declare|delete|drop|end|exec|execute|fetch|insert|kill|open|select|sys|sysobjects|syscolumns|table|update)", RegexOption.IGNORE_CASE)
    
    /**
     * String değeri sanitize eder.
     * - Null veya boş string'leri olduğu gibi döndürür
     * - Maximum uzunluğu kontrol eder
     * - Tehlikeli karakterleri temizler
     */
    fun sanitizeString(value: String?, maxLength: Int = MAX_STRING_FIELD_LENGTH): String? {
        if (value.isNullOrBlank()) return value
        
        var sanitized = value
        
        // Maximum uzunluk kontrolü
        if (sanitized.length > maxLength) {
            sanitized = sanitized.take(maxLength)
            Logger.i("Input truncated to $maxLength characters")
        }
        
        // Script tag'lerini temizle
        sanitized = sanitized.replace(SCRIPT_PATTERN, "")
        
        // HTML tag'lerini temizle
        sanitized = sanitized.replace(HTML_TAG_PATTERN, "")
        
        // Null bytes temizle
        sanitized = sanitized.replace("\u0000", "")
        
        return sanitized.trim()
    }
    
    /**
     * UserId için özel validation.
     */
    fun sanitizeUserId(userId: String?): String? {
        return sanitizeString(userId, MAX_USER_ID_LENGTH)
    }
    
    /**
     * Keyword için özel validation.
     */
    fun sanitizeKeyword(keyword: String?): String? {
        return sanitizeString(keyword, MAX_KEYWORD_LENGTH)
    }
    
    /**
     * Product list için özel validation.
     */
    fun sanitizeProductList(productList: String?): String? {
        return sanitizeString(productList, MAX_PRODUCT_LIST_LENGTH)
    }
    
    /**
     * Payload için özel validation.
     */
    fun sanitizePayload(payload: String?): String? {
        return sanitizeString(payload, MAX_PAYLOAD_LENGTH)
    }
    
    /**
     * Validation sonuçlarını döndürür.
     * @return Validation hataları listesi (boş liste = valid)
     */
    fun validateEventParams(
        userId: String?,
        lineItemIds: String?,
        productList: String?,
        categoryId: String?,
        keyword: String?,
        loyaltyCard: String?,
        transactionId: String?
    ): List<String> {
        val errors = mutableListOf<String>()
        
        // UserId validation
        if (!userId.isNullOrBlank() && userId.length > MAX_USER_ID_LENGTH) {
            errors.add("userId exceeds maximum length of $MAX_USER_ID_LENGTH")
        }
        
        // ProductList validation
        if (!productList.isNullOrBlank() && productList.length > MAX_PRODUCT_LIST_LENGTH) {
            errors.add("productList exceeds maximum length of $MAX_PRODUCT_LIST_LENGTH")
        }
        
        // Keyword validation
        if (!keyword.isNullOrBlank() && keyword.length > MAX_KEYWORD_LENGTH) {
            errors.add("keyword exceeds maximum length of $MAX_KEYWORD_LENGTH")
        }
        
        // Diğer string field'lar için genel kontrol
        listOf(
            "lineItemIds" to lineItemIds,
            "categoryId" to categoryId,
            "loyaltyCard" to loyaltyCard,
            "transactionId" to transactionId
        ).forEach { (name, value) ->
            if (!value.isNullOrBlank() && value.length > MAX_STRING_FIELD_LENGTH) {
                errors.add("$name exceeds maximum length of $MAX_STRING_FIELD_LENGTH")
            }
        }
        
        return errors
    }
    
    /**
     * Performance event params için validation.
     */
    fun validatePerformanceEventParams(
        lineItemId: String,
        creativeId: String,
        adUnit: String,
        productSku: String,
        payload: String,
        keyword: String?,
        userId: String?
    ): List<String> {
        val errors = mutableListOf<String>()
        
        // Required field'lar boş olmamalı
        if (lineItemId.isBlank()) {
            errors.add("lineItemId is required")
        }
        if (creativeId.isBlank()) {
            errors.add("creativeId is required")
        }
        if (adUnit.isBlank()) {
            errors.add("adUnit is required")
        }
        if (productSku.isBlank()) {
            errors.add("productSku is required")
        }
        if (payload.isBlank()) {
            errors.add("payload is required")
        }
        
        // Maximum uzunluk kontrolleri
        if (payload.length > MAX_PAYLOAD_LENGTH) {
            errors.add("payload exceeds maximum length of $MAX_PAYLOAD_LENGTH")
        }
        
        if (!keyword.isNullOrBlank() && keyword.length > MAX_KEYWORD_LENGTH) {
            errors.add("keyword exceeds maximum length of $MAX_KEYWORD_LENGTH")
        }
        
        if (!userId.isNullOrBlank() && userId.length > MAX_USER_ID_LENGTH) {
            errors.add("userId exceeds maximum length of $MAX_USER_ID_LENGTH")
        }
        
        return errors
    }
    
    /**
     * Potansiyel SQL injection pattern'lerini kontrol eder.
     * @return true eğer tehlikeli pattern bulunursa
     */
    fun containsSqlInjection(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        return SQL_INJECTION_PATTERN.containsMatchIn(value)
    }
}

