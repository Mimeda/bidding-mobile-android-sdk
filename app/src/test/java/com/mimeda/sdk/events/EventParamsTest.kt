package com.mimeda.sdk.events

import org.junit.Test
import org.junit.Assert.*

class EventParamsTest {
    @Test
    fun testEventParamsDefaultValues() {
        val params = EventParams()
        
        assertNull(params.userId)
        assertNull(params.lineItemIds)
        assertNull(params.productList)
        assertNull(params.categoryId)
        assertNull(params.keyword)
        assertNull(params.loyaltyCard)
        assertNull(params.transactionId)
        assertNull(params.totalRowCount)
    }
    
    @Test
    fun testEventParamsWithAllValues() {
        val params = EventParams(
            userId = "user-123",
            lineItemIds = "6817,6818",
            productList = "SKU123:1:10.50",
            categoryId = "123",
            keyword = "test",
            loyaltyCard = "card-123",
            transactionId = "txn-789",
            totalRowCount = 50
        )
        
        assertEquals("user-123", params.userId)
        assertEquals("6817,6818", params.lineItemIds)
        assertEquals("SKU123:1:10.50", params.productList)
        assertEquals("123", params.categoryId)
        assertEquals("test", params.keyword)
        assertEquals("card-123", params.loyaltyCard)
        assertEquals("txn-789", params.transactionId)
        assertEquals(50, params.totalRowCount)
    }
    
    @Test
    fun testProductListFormat() {
        val singleProduct = EventParams(productList = "SKU123:1:10.50")
        assertEquals("SKU123:1:10.50", singleProduct.productList)
        
        val multipleProducts = EventParams(productList = "SKU123:1:10.50,SKU456:2:25.00")
        assertEquals("SKU123:1:10.50,SKU456:2:25.00", multipleProducts.productList)
    }
}

