package com.mimeda.sdk

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mimeda.sdk.events.EventName
import com.mimeda.sdk.events.EventParameter
import com.mimeda.sdk.events.EventParams
import com.mimeda.sdk.events.PerformanceEventParams
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class MimedaSDKInstrumentedTest {
    private lateinit var context: Context
    
    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        MimedaSDK.shutdown()
        TestHelpers.clearSharedPreferences(context)
    }
    
    @After
    fun tearDown() {
        MimedaSDK.shutdown()
        TestHelpers.clearSharedPreferences(context)
    }
    
    @Test
    fun testSuccessfulInitialization() {
        MimedaSDK.initialize(context, "test-api-key", Environment.PRODUCTION)
        
        assertTrue(MimedaSDK.isInitialized())
    }
    
    @Test
    fun testPackageNameRetrieval() {
        MimedaSDK.initialize(context, "test-key")
        
        assertEquals(context.packageName, com.mimeda.sdk.utils.DeviceInfo.getAppName())
    }
    
    @Test
    fun testSessionIdCreation() {
        MimedaSDK.initialize(context, "test-key")
        
        MimedaSDK.trackEvent(
            EventName.HOME,
            EventParameter.VIEW,
            EventParams()
        )
        
        Thread.sleep(1000)
        
        val sessionId = TestHelpers.getSessionIdFromPrefs(context)
        assertNotNull(sessionId)
        assertTrue(sessionId!!.isNotEmpty())
    }
    
    @Test
    fun testAnonymousIdCreation() {
        MimedaSDK.initialize(context, "test-key")
        
        MimedaSDK.trackEvent(
            EventName.HOME,
            EventParameter.VIEW,
            EventParams()
        )
        
        Thread.sleep(1000)
        
        val anonymousId = TestHelpers.getAnonymousIdFromPrefs(context)
        assertNotNull(anonymousId)
    }
    
    @Test
    fun testEventTrackingWithoutInitialization() {
        MimedaSDK.trackEvent(EventName.HOME, EventParameter.VIEW)
        
        assertFalse(MimedaSDK.isInitialized())
    }
    
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
    fun testAllEventParameters() {
        assertEquals("view", EventParameter.VIEW.value)
        assertEquals("addtocart", EventParameter.ADD_TO_CART.value)
        assertEquals("addtofavorites", EventParameter.ADD_TO_FAVORITES.value)
        assertEquals("success", EventParameter.SUCCESS.value)
    }
    
    @Test
    fun testPerformanceEventTracking() {
        MimedaSDK.initialize(context, "test-api-key")
        
        MimedaSDK.trackPerformanceImpression(
            PerformanceEventParams(
                lineItemId = "6817",
                creativeId = "277",
                adUnit = "test-ad-unit",
                productSku = "SKU123",
                payload = "test-payload"
            )
        )
        
        Thread.sleep(500)
        assertTrue(MimedaSDK.isInitialized())
    }
}

