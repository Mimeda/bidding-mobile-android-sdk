package com.mimeda.sdk.events

import org.junit.Test
import org.junit.Assert.*

class PerformanceEventParamsTest {
    @Test
    fun testPerformanceEventParamsRequiredFields() {
        val params = PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload"
        )
        
        assertEquals("6817", params.lineItemId)
        assertEquals("277", params.creativeId)
        assertEquals("test-ad-unit", params.adUnit)
        assertEquals("SKU123", params.productSku)
        assertEquals("test-payload", params.payload)
    }
    
    @Test
    fun testPerformanceEventParamsOptionalFields() {
        val params = PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload",
            keyword = "test",
            userId = "user-123"
        )
        
        assertEquals("test", params.keyword)
        assertEquals("user-123", params.userId)
    }
    
    @Test
    fun testPerformanceEventParamsDefaultOptionalFields() {
        val params = PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload"
        )
        
        assertNull(params.keyword)
        assertNull(params.userId)
    }
}

