package com.mimeda.sdk.events

data class PerformanceEventParams(
    val lineItemId: String,
    val creativeId: String,
    val adUnit: String,
    val productSku: String,
    val payload: String,
    val keyword: String? = null,
    val userId: String? = null
)

