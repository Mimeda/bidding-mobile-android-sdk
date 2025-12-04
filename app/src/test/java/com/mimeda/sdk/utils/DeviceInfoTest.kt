package com.mimeda.sdk.utils

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

import org.junit.Assert.*

@RunWith(MockitoJUnitRunner.Silent::class)
class DeviceInfoTest {
    @Mock
    private lateinit var context: Context
    
    @Mock
    private lateinit var contentResolver: android.content.ContentResolver
    
    @Before
    fun setUp() {
        whenever(context.contentResolver).thenReturn(contentResolver)
        whenever(context.packageName).thenReturn("com.test.app")
    }
    
    @Test
    fun testOsInformation() {
        val os = DeviceInfo.getOs()
        assertEquals("Android", os)
    }
    
    @Test
    fun testBrowserInformation() {
        val browser = DeviceInfo.getBrowser()
        assertNull(browser)
    }
    
    @Test
    fun testLanguageInformation() {
        val language = DeviceInfo.getLanguage()
        assertTrue(language.isNotEmpty())
        assertTrue(language.contains("-"))
    }
    
    @Test
    fun testAppNameRetrieval() {
        DeviceInfo.initialize(context)
        val appName = DeviceInfo.getAppName()
        assertEquals("com.test.app", appName)
    }
    
    @Test
    fun testDeviceIdIsNotEmpty() {
        DeviceInfo.initialize(context)
        val deviceId = DeviceInfo.getDeviceId()
        
        assertNotNull(deviceId)
        assertTrue(deviceId.isNotEmpty())
    }
    
    @Test
    fun testDeviceIdConsistency() {
        DeviceInfo.initialize(context)
        val deviceId1 = DeviceInfo.getDeviceId()
        val deviceId2 = DeviceInfo.getDeviceId()
        
        assertEquals(deviceId1, deviceId2)
    }
}

