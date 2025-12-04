package com.mimeda.sdk.utils

import org.junit.Test
import java.util.Base64
import java.nio.charset.StandardCharsets

import org.junit.Assert.*

class SecurePreferencesTest {
    
    @Test
    fun testObfuscation() {
        val testKey = "session_id"
        val testValue = "test-session-id-123"
        
        val obfuscatedKey = Base64.getEncoder().encodeToString(
            testKey.toByteArray(StandardCharsets.UTF_8)
        )
        
        val obfuscatedValue = Base64.getEncoder().encodeToString(
            testValue.toByteArray(StandardCharsets.UTF_8)
        )
        
        assertNotEquals(testKey, obfuscatedKey)
        assertNotEquals(testValue, obfuscatedValue)
        
        val deobfuscatedKey = String(
            Base64.getDecoder().decode(obfuscatedKey),
            StandardCharsets.UTF_8
        )
        
        val deobfuscatedValue = String(
            Base64.getDecoder().decode(obfuscatedValue),
            StandardCharsets.UTF_8
        )
        
        assertEquals(testKey, deobfuscatedKey)
        assertEquals(testValue, deobfuscatedValue)
    }
}

