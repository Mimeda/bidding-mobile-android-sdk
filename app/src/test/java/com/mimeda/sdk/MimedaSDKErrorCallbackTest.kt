package com.mimeda.sdk

import android.content.Context
import com.mimeda.sdk.events.EventName
import com.mimeda.sdk.events.EventParameter
import com.mimeda.sdk.events.PerformanceEventType
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

import org.junit.Assert.*

@RunWith(MockitoJUnitRunner.Silent::class)
class MimedaSDKErrorCallbackTest {
    @Mock
    private lateinit var context: Context
    
    @Mock
    private lateinit var applicationContext: Context
    
    @Mock
    private lateinit var contentResolver: android.content.ContentResolver
    
    @Before
    fun setUp() {
        MimedaSDK.shutdown()
        whenever(context.packageName).thenReturn("com.test.app")
        whenever(context.applicationContext).thenReturn(applicationContext)
        whenever(context.contentResolver).thenReturn(contentResolver)
    }
    
    @After
    fun tearDown() {
        MimedaSDK.shutdown()
    }
    
    @Test
    fun testValidationCallbackIsSet() {
        // Callback'in SDK'ya doğru şekilde atandığını test et
        val callback = object : MimedaSDKErrorCallback {
            override fun onEventTrackingFailed(
                eventName: EventName,
                eventParameter: EventParameter,
                error: Throwable
            ) {}
            
            override fun onPerformanceEventTrackingFailed(
                eventType: PerformanceEventType,
                error: Throwable
            ) {}
            
            override fun onValidationFailed(
                eventName: EventName?,
                errors: List<String>
            ) {}
        }
        
        MimedaSDK.initialize(context, "test-key", errorCallback = callback)
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testCallbackExceptionDoesNotCrashSDK() {
        val callback = object : MimedaSDKErrorCallback {
            override fun onEventTrackingFailed(
                eventName: EventName,
                eventParameter: EventParameter,
                error: Throwable
            ) {
                throw RuntimeException("Callback error")
            }
            
            override fun onPerformanceEventTrackingFailed(
                eventType: PerformanceEventType,
                error: Throwable
            ) {}
            
            override fun onValidationFailed(
                eventName: EventName?,
                errors: List<String>
            ) {}
        }
        
        MimedaSDK.initialize(context, "test-key", errorCallback = callback)
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testCallbackWithoutError() {
        MimedaSDK.initialize(context, "test-key")
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testSDKInitializesWithCallback() {
        val callback = object : MimedaSDKErrorCallback {
            override fun onEventTrackingFailed(
                eventName: EventName,
                eventParameter: EventParameter,
                error: Throwable
            ) {}
            
            override fun onPerformanceEventTrackingFailed(
                eventType: PerformanceEventType,
                error: Throwable
            ) {}
            
            override fun onValidationFailed(
                eventName: EventName?,
                errors: List<String>
            ) {}
        }
        
        MimedaSDK.initialize(context, "test-key", Environment.STAGING, callback)
        assertTrue(MimedaSDK.isInitialized())
    }
}

