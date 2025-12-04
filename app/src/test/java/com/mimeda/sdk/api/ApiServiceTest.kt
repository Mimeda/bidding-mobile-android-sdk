package com.mimeda.sdk.api

import com.mimeda.sdk.Environment
import com.mimeda.sdk.events.EventName
import com.mimeda.sdk.events.EventParameter
import com.mimeda.sdk.events.EventParams
import com.mimeda.sdk.events.EventType
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
    fun testValidationFailure() {
        val result = apiService.trackEvent(
            EventName.HOME,
            EventParameter.VIEW,
            EventParams(),
            EventType.EVENT,
            "", // Boş app name
            "test-device",
            "Android",
            "tr-TR",
            "test-session",
            "test-anonymous"
        )
        
        assertTrue(result)
        assertEquals(0, mockServer.requestCount)
    }
}

