package com.mimeda.sdk.api

import com.mimeda.sdk.Environment
import com.mimeda.sdk.MimedaSDKErrorCallback
import com.mimeda.sdk.events.EventName
import com.mimeda.sdk.events.EventParameter
import com.mimeda.sdk.events.EventParams
import com.mimeda.sdk.events.EventType
import com.mimeda.sdk.events.PerformanceEventParams
import com.mimeda.sdk.events.PerformanceEventType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

import org.junit.Assert.*

class ApiServiceTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var client: OkHttpClient
    
    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()
        
        client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .build()
        
        val baseUrl = mockServer.url("/").toString().removeSuffix("/")
        apiService = ApiService(client, Environment.STAGING, testEventBaseUrl = baseUrl, testPerformanceBaseUrl = baseUrl)
    }
    
    @After
    fun tearDown() {
        mockServer.shutdown()
    }
    
    @Test
    fun testSuccessResponse() {
        mockServer.enqueue(MockResponse().setResponseCode(200))
        
        val result = apiService.trackEvent(
            EventName.HOME,
            EventParameter.VIEW,
            EventParams(),
            EventType.EVENT,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertTrue(result)
        assertEquals(1, mockServer.requestCount)
    }
    
    @Test
    fun testClientErrorResponse() {
        mockServer.enqueue(MockResponse().setResponseCode(400))
        
        val result = apiService.trackEvent(
            EventName.HOME,
            EventParameter.VIEW,
            EventParams(),
            EventType.EVENT,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertFalse(result)
        assertEquals(1, mockServer.requestCount)
    }
    
    @Test
    fun testServerErrorResponse() {
        mockServer.enqueue(MockResponse().setResponseCode(500))
        mockServer.enqueue(MockResponse().setResponseCode(500))
        mockServer.enqueue(MockResponse().setResponseCode(500))
        mockServer.enqueue(MockResponse().setResponseCode(500))
        
        val result = apiService.trackEvent(
            EventName.HOME,
            EventParameter.VIEW,
            EventParams(),
            EventType.EVENT,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertFalse(result)
    }
    
    @Test
    fun testAllEventNames() {
        EventName.values().forEach { eventName ->
            mockServer.enqueue(MockResponse().setResponseCode(200))
            
            val result = apiService.trackEvent(
                eventName,
                EventParameter.VIEW,
                EventParams(),
                EventType.EVENT,
                "test-app",
                "test-device",
                "Android",
                "tr-TR",
                "test-session",
                "test-anonymous"
            )
            
            assertTrue(result)
        }
    }
    
    @Test
    fun testAllEventParameters() {
        EventParameter.values().forEach { eventParameter ->
            mockServer.enqueue(MockResponse().setResponseCode(200))
            
            val result = apiService.trackEvent(
                EventName.HOME,
                eventParameter,
                EventParams(),
                EventType.EVENT,
                "test-app",
                "test-device",
                "Android",
                "tr-TR",
                "test-session",
                "test-anonymous"
            )
            
            assertTrue(result)
        }
    }
    
    @Test
    fun testEventWithAllParams() {
        mockServer.enqueue(MockResponse().setResponseCode(200))
        
        val params = EventParams(
            userId = "user-123",
            lineItemIds = "6817,6818",
            productList = "SKU123:1:10.50",
            categoryId = "cat-123",
            keyword = "test",
            loyaltyCard = "card-123",
            transactionId = "txn-123",
            totalRowCount = 100
        )
        
        val result = apiService.trackEvent(
            EventName.SEARCH,
            EventParameter.VIEW,
            params,
            EventType.EVENT,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertTrue(result)
        
        val request = mockServer.takeRequest()
        val url = request.requestUrl.toString()
        
        assertTrue(url.contains("uid=user-123"))
        assertTrue(url.contains("kw=test"))
        assertTrue(url.contains("ct=cat-123"))
    }
    
    @Test
    fun testPerformanceImpressionSuccess() {
        mockServer.enqueue(MockResponse().setResponseCode(200))
        
        val params = PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload"
        )
        
        val result = apiService.trackPerformanceEvent(
            PerformanceEventType.IMPRESSION,
            params,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertTrue(result)
        
        val request = mockServer.takeRequest()
        assertTrue(request.path!!.contains("impressions"))
    }
    
    @Test
    fun testPerformanceClickSuccess() {
        mockServer.enqueue(MockResponse().setResponseCode(200))
        
        val params = PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload"
        )
        
        val result = apiService.trackPerformanceEvent(
            PerformanceEventType.CLICK,
            params,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertTrue(result)
        
        val request = mockServer.takeRequest()
        assertTrue(request.path!!.contains("clicks"))
    }
    
    @Test
    fun testPerformanceEventWithOptionalParams() {
        mockServer.enqueue(MockResponse().setResponseCode(200))
        
        val params = PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
            payload = "test-payload",
            keyword = "test-keyword",
            userId = "user-123"
        )
        
        val result = apiService.trackPerformanceEvent(
            PerformanceEventType.IMPRESSION,
            params,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertTrue(result)
        
        val request = mockServer.takeRequest()
        val url = request.requestUrl.toString()
        assertTrue(url.contains("kw=test-keyword"))
        assertTrue(url.contains("uid=user-123"))
    }
    
    @Test
    fun testPerformanceEventWithEmptyParams() {
        mockServer.enqueue(MockResponse().setResponseCode(200))
        
        val params = PerformanceEventParams()
        
        val result = apiService.trackPerformanceEvent(
            PerformanceEventType.IMPRESSION,
            params,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertTrue(result)
        assertEquals(1, mockServer.requestCount)
    }
    
    @Test
    fun testEventWithEmptySessionAndAnonymousId() {
        mockServer.enqueue(MockResponse().setResponseCode(200))
        
        val result = apiService.trackEvent(
            EventName.HOME,
            EventParameter.VIEW,
            EventParams(),
            EventType.EVENT,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "",
            ""
        )
        
        assertTrue(result)
        assertEquals(1, mockServer.requestCount)
    }
    
    @Test
    fun testApiServiceWithErrorCallback() {
        var callbackInvoked = false
        
        val callback = object : MimedaSDKErrorCallback {
            override fun onEventTrackingFailed(
                eventName: EventName,
                eventParameter: EventParameter,
                error: Throwable
            ) {
                callbackInvoked = true
            }
            
            override fun onPerformanceEventTrackingFailed(
                eventType: PerformanceEventType,
                error: Throwable
            ) {}
        }
        
        val baseUrl = mockServer.url("/").toString().removeSuffix("/")
        val apiServiceWithCallback = ApiService(client, Environment.STAGING, callback, baseUrl, baseUrl)
        
        mockServer.enqueue(MockResponse().setResponseCode(400))
        
        apiServiceWithCallback.trackEvent(
            EventName.HOME,
            EventParameter.VIEW,
            EventParams(),
            EventType.EVENT,
            "test-app",
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertTrue(callbackInvoked)
    }
    
    @Test
    fun testProductionEnvironment() {
        val productionService = ApiService(client, Environment.PRODUCTION)
        assertNotNull(productionService)
    }
}
