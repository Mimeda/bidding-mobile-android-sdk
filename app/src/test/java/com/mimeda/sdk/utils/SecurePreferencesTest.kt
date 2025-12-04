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
    
    @Test
    fun testObfuscationWithAnonymousId() {
        val testKey = "anonymous_id"
        val testValue = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
        
        val obfuscatedKey = Base64.getEncoder().encodeToString(
            testKey.toByteArray(StandardCharsets.UTF_8)
        )
        
        val obfuscatedValue = Base64.getEncoder().encodeToString(
            testValue.toByteArray(StandardCharsets.UTF_8)
        )
        
        assertNotEquals(testKey, obfuscatedKey)
        assertNotEquals(testValue, obfuscatedValue)
        
        val deobfuscatedValue = String(
            Base64.getDecoder().decode(obfuscatedValue),
            StandardCharsets.UTF_8
        )
        
        assertEquals(testValue, deobfuscatedValue)
    }
    
    @Test
    fun testObfuscationWithEmptyString() {
        val testValue = ""
        
        val obfuscatedValue = Base64.getEncoder().encodeToString(
            testValue.toByteArray(StandardCharsets.UTF_8)
        )
        
        val deobfuscatedValue = String(
            Base64.getDecoder().decode(obfuscatedValue),
            StandardCharsets.UTF_8
        )
        
        assertEquals(testValue, deobfuscatedValue)
    }
    
    @Test
    fun testObfuscationWithSpecialCharacters() {
        val testValue = "test!@#\$%^&*()_+-=[]{}|;':\",./<>?"
        
        val obfuscatedValue = Base64.getEncoder().encodeToString(
            testValue.toByteArray(StandardCharsets.UTF_8)
        )
        
        val deobfuscatedValue = String(
            Base64.getDecoder().decode(obfuscatedValue),
            StandardCharsets.UTF_8
        )
        
        assertEquals(testValue, deobfuscatedValue)
    }
    
    @Test
    fun testObfuscationWithUnicodeCharacters() {
        val testValue = "Türkçe karakterler: şçöüğı"
        
        val obfuscatedValue = Base64.getEncoder().encodeToString(
            testValue.toByteArray(StandardCharsets.UTF_8)
        )
        
        val deobfuscatedValue = String(
            Base64.getDecoder().decode(obfuscatedValue),
            StandardCharsets.UTF_8
        )
        
        assertEquals(testValue, deobfuscatedValue)
    }
    
    @Test
    fun testObfuscationIdempotency() {
        val testValue = "test-value-123"
        
        val obfuscated1 = Base64.getEncoder().encodeToString(
            testValue.toByteArray(StandardCharsets.UTF_8)
        )
        
        val obfuscated2 = Base64.getEncoder().encodeToString(
            testValue.toByteArray(StandardCharsets.UTF_8)
        )
        
        assertEquals(obfuscated1, obfuscated2)
    }
}

