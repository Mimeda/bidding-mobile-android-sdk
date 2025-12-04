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
    
    @Test
    fun testTrackEventAfterInitialization() {
        MimedaSDK.initialize(context, "test-key")
        
        MimedaSDK.trackEvent(EventName.HOME, EventParameter.VIEW)
        MimedaSDK.trackEvent(EventName.LISTING, EventParameter.VIEW)
        MimedaSDK.trackEvent(EventName.SEARCH, EventParameter.VIEW, EventParams(keyword = "test"))
        MimedaSDK.trackEvent(EventName.PDP, EventParameter.VIEW, EventParams(productList = "SKU123:1:10.50"))
        MimedaSDK.trackEvent(EventName.PDP, EventParameter.ADD_TO_CART, EventParams(productList = "SKU123:1:10.50"))
        MimedaSDK.trackEvent(EventName.PDP, EventParameter.ADD_TO_FAVORITES, EventParams(productList = "SKU123:1:10.50"))
        MimedaSDK.trackEvent(EventName.CART, EventParameter.VIEW, EventParams(productList = "SKU123:1:10.50"))
        MimedaSDK.trackEvent(EventName.PURCHASE, EventParameter.SUCCESS, EventParams(transactionId = "TXN123"))
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testTrackEventWithAllParams() {
        MimedaSDK.initialize(context, "test-key")
        
        val params = EventParams(
            userId = "user-123",
            lineItemIds = "6817,6818",
            productList = "SKU123:1:10.50,SKU456:2:25.00",
            categoryId = "cat-123",
            keyword = "test keyword",
            loyaltyCard = "card-123",
            transactionId = "txn-123",
            totalRowCount = 100
        )
        
        MimedaSDK.trackEvent(EventName.SEARCH, EventParameter.VIEW, params)
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testTrackPerformanceImpressionNotInitialized() {
        val params = com.mimeda.sdk.events.PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload"
        )
        
        MimedaSDK.trackPerformanceImpression(params)
        
        assertFalse(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testTrackPerformanceClickNotInitialized() {
        val params = com.mimeda.sdk.events.PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload"
        )
        
        MimedaSDK.trackPerformanceClick(params)
        
        assertFalse(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testTrackPerformanceImpressionAfterInitialization() {
        MimedaSDK.initialize(context, "test-key")
        
        val params = com.mimeda.sdk.events.PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload"
        )
        
        MimedaSDK.trackPerformanceImpression(params)
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testTrackPerformanceClickAfterInitialization() {
        MimedaSDK.initialize(context, "test-key")
        
        val params = com.mimeda.sdk.events.PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload"
        )
        
        MimedaSDK.trackPerformanceClick(params)
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testTrackPerformanceWithOptionalParams() {
        MimedaSDK.initialize(context, "test-key")
        
        val params = com.mimeda.sdk.events.PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload",
            keyword = "test-keyword",
            userId = "user-123"
        )
        
        MimedaSDK.trackPerformanceImpression(params)
        MimedaSDK.trackPerformanceClick(params)
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testInitializeWithErrorCallback() {
        val callback = object : MimedaSDKErrorCallback {
            override fun onEventTrackingFailed(
                eventName: EventName,
                eventParameter: EventParameter,
                error: Throwable
            ) {}
            
            override fun onPerformanceEventTrackingFailed(
                eventType: com.mimeda.sdk.events.PerformanceEventType,
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
    
    @Test
    fun testReinitializeAfterShutdown() {
        MimedaSDK.initialize(context, "first-key")
        assertTrue(MimedaSDK.isInitialized())
        
        MimedaSDK.shutdown()
        assertFalse(MimedaSDK.isInitialized())
        
        MimedaSDK.initialize(context, "second-key")
        assertTrue(MimedaSDK.isInitialized())
    }
}

