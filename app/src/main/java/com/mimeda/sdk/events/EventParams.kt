package com.mimeda.sdk.events

data class EventParams(
    val userId: String? = null,
    val lineItemIds: String? = null,
    val productList: String? = null,
    val categoryId: String? = null,
    val keyword: String? = null,
    val loyaltyCard: String? = null,
    val transactionId: String? = null,
    val totalRowCount: Int? = null,
    val app: String? = null
)

