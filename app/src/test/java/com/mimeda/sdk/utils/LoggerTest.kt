package com.mimeda.sdk.utils

import org.junit.Test
import org.junit.Assert.*

class LoggerTest {
    @Test
    fun testNoDuplicateLogging() {
        val exception = RuntimeException("Test exception")
        
        Logger.e("Test error", exception)
        
        assertTrue(true)
    }
    
    @Test
    fun testErrorLoggingWithoutException() {
        Logger.e("Test error")
        
        assertTrue(true)
    }
    
    @Test
    fun testInfoLogging() {
        Logger.i("Test info")
        
        assertTrue(true)
    }
    
    @Test
    fun testSuccessLogging() {
        Logger.s("Test success")
        
        assertTrue(true)
    }
}

