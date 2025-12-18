package com.mimeda.sdk.utils

internal object InputValidator {
    
    private const val MAX_USER_ID_LENGTH = 256
    private const val MAX_STRING_FIELD_LENGTH = 1024
    private const val MAX_PRODUCT_LIST_LENGTH = 10240
    private const val MAX_KEYWORD_LENGTH = 256
    private const val MAX_PAYLOAD_LENGTH = 65536
    
    private val SCRIPT_PATTERN = Regex("<script[^>]*>.*?</script>", RegexOption.IGNORE_CASE)
    private val HTML_TAG_PATTERN = Regex("<[^>]+>")
    private val SQL_INJECTION_PATTERN = Regex(
        "('|--|;|/\\*|\\*/|@@|char|nchar|varchar|nvarchar|alter|begin|cast|create|cursor|declare|delete|drop|end|exec|execute|fetch|insert|kill|open|select|sys|sysobjects|syscolumns|table|update)",
        RegexOption.IGNORE_CASE
    )
    
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
        sanitized = sanitizeSqlInjection(sanitized) ?: sanitized
        
        return sanitized.trim()
    }
    
    private fun sanitizeSqlInjection(value: String?): String? {
        if (value.isNullOrBlank()) return value
        return value.replace(SQL_INJECTION_PATTERN, "")
    }
    
    fun containsSqlInjection(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        return SQL_INJECTION_PATTERN.containsMatchIn(value)
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
}
