package com.mimeda.sdk

import org.junit.Test
import org.junit.Assert.*

class EnvironmentTest {
    @Test
    fun testEnvironmentValues() {
        assertEquals(Environment.PRODUCTION, Environment.valueOf("PRODUCTION"))
        assertEquals(Environment.STAGING, Environment.valueOf("STAGING"))
    }
    
    @Test
    fun testEnvironmentCount() {
        assertEquals(2, Environment.values().size)
    }
}

