package com.mimeda.sdk.events

import org.junit.Test
import org.junit.Assert.*

class EventNameTest {
    @Test
    fun testAllEventNames() {
        assertEquals("home", EventName.HOME.value)
        assertEquals("listing", EventName.LISTING.value)
        assertEquals("search", EventName.SEARCH.value)
        assertEquals("pdp", EventName.PDP.value)
        assertEquals("cart", EventName.CART.value)
        assertEquals("purchase", EventName.PURCHASE.value)
    }
    
    @Test
    fun testEventNameValuesAreNotEmpty() {
        EventName.values().forEach { eventName ->
            assertTrue("Event name value should not be empty: ${eventName.name}", eventName.value.isNotEmpty())
        }
    }
}

