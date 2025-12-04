package com.mimeda.sdk.events

import org.junit.Test
import org.junit.Assert.*

class EventParameterTest {
    @Test
    fun testAllEventParameters() {
        assertEquals("view", EventParameter.VIEW.value)
        assertEquals("addtocart", EventParameter.ADD_TO_CART.value)
        assertEquals("addtofavorites", EventParameter.ADD_TO_FAVORITES.value)
        assertEquals("success", EventParameter.SUCCESS.value)
    }
    
    @Test
    fun testEventParameterValuesAreNotEmpty() {
        EventParameter.values().forEach { parameter ->
            assertTrue("Event parameter value should not be empty: ${parameter.name}", parameter.value.isNotEmpty())
        }
    }
}

