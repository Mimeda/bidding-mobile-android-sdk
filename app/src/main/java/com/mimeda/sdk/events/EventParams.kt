package com.mimeda.sdk.events

data class EventParams(
    val sdkVersion: String? = null,
    val app: String? = null,
    val timestamp: Long? = null,
    val deviceId: String? = null,
    val os: String? = null,
    val browser: String? = null,
    val language: String? = null,
    val anonymousId: String? = null,
    val userId: String? = null,
    val lineItemIds: String? = null,
    val productList: String? = null,
    val categoryId: String? = null,
    val keyword: String? = null,
    val loyaltyCard: String? = null,
    val transactionId: String? = null,
    val totalRowCount: Int? = null
)

