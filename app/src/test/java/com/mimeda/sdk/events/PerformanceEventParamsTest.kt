package com.mimeda.sdk.events

import org.junit.Test
import org.junit.Assert.*

class PerformanceEventParamsTest {
    @Test
    fun testPerformanceEventParamsWithAllFields() {
        val params = PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload",
            keyword = "test",
            userId = "user-123"
        )
        
        assertEquals("6817", params.lineItemId)
        assertEquals("277", params.creativeId)
        assertEquals("test-ad-unit", params.adUnit)
        assertEquals("SKU123", params.productSku)
        assertEquals("test-payload", params.payload)
        assertEquals("test", params.keyword)
        assertEquals("user-123", params.userId)
    }
    
    @Test
    fun testPerformanceEventParamsWithPartialFields() {
        val params = PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit"
        )
        
        assertEquals("6817", params.lineItemId)
        assertEquals("277", params.creativeId)
        assertEquals("test-ad-unit", params.adUnit)
        assertNull(params.productSku)
        assertNull(params.payload)
        assertNull(params.keyword)
        assertNull(params.userId)
    }
    
    @Test
    fun testPerformanceEventParamsAllNullable() {
        val params = PerformanceEventParams()
        
        assertNull(params.lineItemId)
        assertNull(params.creativeId)
        assertNull(params.adUnit)
        assertNull(params.productSku)
        assertNull(params.payload)
        assertNull(params.keyword)
        assertNull(params.userId)
    }
    
    @Test
    fun testPerformanceEventParamsOnlyKeywordAndUserId() {
        val params = PerformanceEventParams(
            keyword = "search-term",
            userId = "user-456"
        )
        
        assertNull(params.lineItemId)
        assertNull(params.creativeId)
        assertNull(params.adUnit)
        assertNull(params.productSku)
        assertNull(params.payload)
        assertEquals("search-term", params.keyword)
        assertEquals("user-456", params.userId)
    }
}
