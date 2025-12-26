package com.mimeda.sdk.events

data class PerformanceEventParams(
    val lineItemId: String? = null,
    val creativeId: String? = null,
    val adUnit: String? = null,
    val productSku: String? = null,
    val payload: String? = null,
    val keyword: String? = null,
    val userId: String? = null,
    val app: String? = null
)
