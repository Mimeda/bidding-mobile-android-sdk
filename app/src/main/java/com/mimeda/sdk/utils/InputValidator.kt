package com.mimeda.sdk.utils

internal object InputValidator {
    
    private const val MAX_USER_ID_LENGTH = 256
    private const val MAX_STRING_FIELD_LENGTH = 1024
    private const val MAX_PRODUCT_LIST_LENGTH = 10240
    private const val MAX_KEYWORD_LENGTH = 256
    private const val MAX_PAYLOAD_LENGTH = 65536
    
    private val SCRIPT_PATTERN = Regex("<script[^>]*>.*?</script>", RegexOption.IGNORE_CASE)
    private val HTML_TAG_PATTERN = Regex("<[^>]+>")
    private val SQL_INJECTION_PATTERN = Regex("('|--|;|/\\*|\\*/|@@|@|char|nchar|varchar|nvarchar|alter|begin|cast|create|cursor|declare|delete|drop|end|exec|execute|fetch|insert|kill|open|select|sys|sysobjects|syscolumns|table|update)", RegexOption.IGNORE_CASE)
    
    fun sanitizeString(value: String?, maxLength: Int = MAX_STRING_FIELD_LENGTH): String? {
        if (value.isNullOrBlank()) return value
        
        var sanitized = value
        
        if (sanitized.length > maxLength) {
            sanitized = sanitized.take(maxLength)
            Logger.i("Input truncated to $maxLength characters")
        }
        
        sanitized = sanitized.replace(SCRIPT_PATTERN, "")
        sanitized = sanitized.replace(HTML_TAG_PATTERN, "")
        sanitized = sanitized.replace("\u0000", "")
        
        return sanitized.trim()
    }
    
    fun sanitizeUserId(userId: String?): String? {
        return sanitizeString(userId, MAX_USER_ID_LENGTH)
    }
    
    fun sanitizeKeyword(keyword: String?): String? {
        return sanitizeString(keyword, MAX_KEYWORD_LENGTH)
    }
    
    fun sanitizeProductList(productList: String?): String? {
        return sanitizeString(productList, MAX_PRODUCT_LIST_LENGTH)
    }
    
    fun sanitizePayload(payload: String?): String? {
        return sanitizeString(payload, MAX_PAYLOAD_LENGTH)
    }
    
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
        
        if (!userId.isNullOrBlank() && userId.length > MAX_USER_ID_LENGTH) {
            errors.add("userId exceeds maximum length of $MAX_USER_ID_LENGTH")
        }
        
        if (!productList.isNullOrBlank() && productList.length > MAX_PRODUCT_LIST_LENGTH) {
            errors.add("productList exceeds maximum length of $MAX_PRODUCT_LIST_LENGTH")
        }
        
        if (!keyword.isNullOrBlank() && keyword.length > MAX_KEYWORD_LENGTH) {
            errors.add("keyword exceeds maximum length of $MAX_KEYWORD_LENGTH")
        }
        
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
    
    fun containsSqlInjection(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        return SQL_INJECTION_PATTERN.containsMatchIn(value)
    }
}
