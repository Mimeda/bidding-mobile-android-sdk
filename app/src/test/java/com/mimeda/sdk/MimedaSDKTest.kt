package com.mimeda.sdk

import android.content.Context
import com.mimeda.sdk.events.EventName
import com.mimeda.sdk.events.EventParameter
import com.mimeda.sdk.events.EventParams
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

import org.junit.Assert.*

@RunWith(MockitoJUnitRunner.Silent::class)
class MimedaSDKTest {
    @Mock
    private lateinit var context: Context
    
    @Mock
    private lateinit var applicationContext: Context
    
    @Before
    fun setUp() {
        MimedaSDK.shutdown()
        whenever(context.packageName).thenReturn("com.test.app")
        whenever(context.applicationContext).thenReturn(applicationContext)
    }
    
    @After
    fun tearDown() {
        MimedaSDK.shutdown()
    }
    
    @Test
    fun testSuccessfulInitialization() {
        MimedaSDK.initialize(context, "test-api-key", Environment.PRODUCTION)
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testProductionEnvironmentInitialization() {
        MimedaSDK.initialize(context, "test-api-key", Environment.PRODUCTION)
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testStagingEnvironmentInitialization() {
        MimedaSDK.initialize(context, "test-api-key", Environment.STAGING)
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testDefaultEnvironmentIsProduction() {
        MimedaSDK.initialize(context, "test-api-key")
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testMultipleInitializationCalls() {
        MimedaSDK.initialize(context, "first-key", Environment.PRODUCTION)
        val firstInit = MimedaSDK.isInitialized()
        
        MimedaSDK.initialize(context, "second-key", Environment.STAGING)
        val secondInit = MimedaSDK.isInitialized()
        
        assertTrue(firstInit)
        assertTrue(secondInit)
    }
    
    @Test
    fun testBlankApiKey() {
        MimedaSDK.initialize(context, "   ", Environment.PRODUCTION)
        
        assertFalse(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testEmptyApiKey() {
        MimedaSDK.initialize(context, "", Environment.PRODUCTION)
        
        assertFalse(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testSdkNotInitialized() {
        MimedaSDK.trackEvent(EventName.HOME, EventParameter.VIEW)
        
        assertFalse(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testMultipleShutdown() {
        MimedaSDK.initialize(context, "test-key")
        assertTrue(MimedaSDK.isInitialized())
        
        MimedaSDK.shutdown()
        assertFalse(MimedaSDK.isInitialized())
        
        MimedaSDK.shutdown()
        assertFalse(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testIsInitialized() {
        assertFalse(MimedaSDK.isInitialized())
        
        MimedaSDK.initialize(context, "test-key")
        assertTrue(MimedaSDK.isInitialized())
        
        MimedaSDK.shutdown()
        assertFalse(MimedaSDK.isInitialized())
    }
}

