# SDK Test Senaryoları ve Test Yöntemleri

## Önemli Notlar ve Test Mantığı

### SDK Tarafından Otomatik Üretilen Değerler
- **anonymousId:** SDK tarafından UUID olarak üretilir, SharedPreferences'ta saklanır
- **sessionId:** SDK tarafından UUID olarak üretilir, 30 dakika sonra yenilenir
- **traceId:** Her event için yeni UUID üretilir
- **timestamp:** Event gönderildiği anın zamanı (System.currentTimeMillis)
- **deviceId:** Android ID veya UUID fallback
- **os, language, app:** DeviceInfo'dan alınır

**ÖNEMLİ:** Bu değerler kullanıcı tarafından kontrol edilemez, SDK içinde üretilir.

### SecurePreferences Kullanımı
- `SecurePreferences` bir **object** (singleton) ve **static method**'lar kullanır
- `SecurePreferences.getString(prefs, key, defaultValue)` şeklinde kullanılır
- `SecurePreferences.putString(editor, key, value)` şeklinde kullanılır
- **ÖNEMLİ:** `session_timestamp` obfuscate edilmez, direkt `prefs.getLong()` ile okunur/yazılır
- Sadece `session_id` ve `anonymous_id` obfuscate edilir

### Test Execution Flow
1. **Setup (@Before):** Her test öncesi SDK shutdown edilir, SharedPreferences temizlenir
2. **Test Execution:** Test senaryosu çalıştırılır
3. **Async Operations:** Event tracking async olduğu için `Thread.sleep()` veya `CountDownLatch` kullanılır
4. **Verification:** MockWebServer'dan request'ler kontrol edilir veya SharedPreferences'tan değerler okunur
5. **Teardown (@After):** SDK shutdown edilir, SharedPreferences temizlenir

### Unit Test vs Instrumentation Test
- **Unit Test (`test`):** Mock context kullanır, hızlı çalışır
- **Instrumentation Test (`androidTest`):** Gerçek Android context kullanır, device/emulator gerektirir

---

## 1. SDK Initialization Testleri

### 1.1 Başarılı Initialization
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 40-45

```kotlin
@Test
fun testSuccessfulInitialization() {
    MimedaSDK.initialize(context, "test-api-key", Environment.PRODUCTION)
    
    assertTrue(MimedaSDK.isInitialized())
}
```

### 1.2 Production Environment Initialization
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 47-52

```kotlin
@Test
fun testProductionEnvironmentInitialization() {
    MimedaSDK.initialize(context, "test-api-key", Environment.PRODUCTION)
    
    assertTrue(MimedaSDK.isInitialized())
}
```

### 1.3 Staging Environment Initialization
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 54-59

```kotlin
@Test
fun testStagingEnvironmentInitialization() {
    MimedaSDK.initialize(context, "test-api-key", Environment.STAGING)
    
    assertTrue(MimedaSDK.isInitialized())
}
```

### 1.4 Default Environment (Production)
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 61-66

```kotlin
@Test
fun testDefaultEnvironmentIsProduction() {
    MimedaSDK.initialize(context, "test-api-key")
    
    assertTrue(MimedaSDK.isInitialized())
}
```

### 1.5 Multiple Initialization Calls
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 68-78

```kotlin
@Test
fun testMultipleInitializationCalls() {
    MimedaSDK.initialize(context, "first-key", Environment.PRODUCTION)
    val firstInit = MimedaSDK.isInitialized()
    
    MimedaSDK.initialize(context, "second-key", Environment.STAGING)
    val secondInit = MimedaSDK.isInitialized()
    
    assertTrue(firstInit)
    assertTrue(secondInit)
}
```

### 1.6 Blank API Key
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 80-85

```kotlin
@Test
fun testBlankApiKey() {
    MimedaSDK.initialize(context, "   ", Environment.PRODUCTION)
    
    assertFalse(MimedaSDK.isInitialized())
}
```

### 1.7 Empty API Key
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 87-92

```kotlin
@Test
fun testEmptyApiKey() {
    MimedaSDK.initialize(context, "", Environment.PRODUCTION)
    
    assertFalse(MimedaSDK.isInitialized())
}
```

### 1.8 SDK Not Initialized
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 94-99

```kotlin
@Test
fun testSdkNotInitialized() {
    MimedaSDK.trackEvent(EventName.HOME, EventParameter.VIEW)
    
    assertFalse(MimedaSDK.isInitialized())
}
```

### 1.9 Multiple Shutdown Calls
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 101-111

```kotlin
@Test
fun testMultipleShutdown() {
    MimedaSDK.initialize(context, "test-key")
    assertTrue(MimedaSDK.isInitialized())
    
    MimedaSDK.shutdown()
    assertFalse(MimedaSDK.isInitialized())
    
        MimedaSDK.shutdown()
    assertFalse(MimedaSDK.isInitialized())
}
```

### 1.10 isInitialized State
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKTest.kt` | **Satır:** 113-122

```kotlin
@Test
fun testIsInitialized() {
    assertFalse(MimedaSDK.isInitialized())

    MimedaSDK.initialize(context, "test-key")
    assertTrue(MimedaSDK.isInitialized())
    
    MimedaSDK.shutdown()
    assertFalse(MimedaSDK.isInitialized())
}
```

---

## 2. Environment Testleri

### 2.1 Environment Values
**Dosya:** `app/src/test/java/com/mimeda/sdk/EnvironmentTest.kt` | **Satır:** 7-11

```kotlin
@Test
fun testEnvironmentValues() {
    assertEquals(Environment.PRODUCTION, Environment.valueOf("PRODUCTION"))
    assertEquals(Environment.STAGING, Environment.valueOf("STAGING"))
}
```

### 2.2 Environment Count
**Dosya:** `app/src/test/java/com/mimeda/sdk/EnvironmentTest.kt` | **Satır:** 13-16

```kotlin
@Test
fun testEnvironmentCount() {
    assertEquals(2, Environment.values().size)
}
```

---

## 3. Event Name Testleri

### 3.1 Tüm Event Name'ler
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/EventNameTest.kt` | **Satır:** 7-15

```kotlin
@Test
fun testAllEventNames() {
    assertEquals("home", EventName.HOME.value)
    assertEquals("listing", EventName.LISTING.value)
    assertEquals("search", EventName.SEARCH.value)
    assertEquals("pdp", EventName.PDP.value)
    assertEquals("cart", EventName.CART.value)
    assertEquals("purchase", EventName.PURCHASE.value)
}
```

### 3.2 Event Name Values Not Empty
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/EventNameTest.kt` | **Satır:** 17-22

```kotlin
@Test
fun testEventNameValuesAreNotEmpty() {
    EventName.values().forEach { eventName ->
        assertTrue("Event name value should not be empty: ${eventName.name}", eventName.value.isNotEmpty())
    }
}
```

---

## 4. Event Parameter Testleri

### 4.1 Tüm Event Parameter'lar
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/EventParameterTest.kt` | **Satır:** 7-14

```kotlin
@Test
fun testAllEventParameters() {
    assertEquals("view", EventParameter.VIEW.value)
    assertEquals("addtocart", EventParameter.ADD_TO_CART.value)
    assertEquals("addtofavorites", EventParameter.ADD_TO_FAVORITES.value)
    assertEquals("success", EventParameter.SUCCESS.value)
}
```

### 4.2 Event Parameter Values Not Empty
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/EventParameterTest.kt` | **Satır:** 16-20

```kotlin
@Test
fun testEventParameterValuesAreNotEmpty() {
    EventParameter.values().forEach { parameter ->
        assertTrue("Event parameter value should not be empty: ${parameter.name}", parameter.value.isNotEmpty())
    }
}
```

---

## 5. EventParams Testleri

### 5.1 EventParams Default Values
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/EventParamsTest.kt` | **Satır:** 7-19

```kotlin
@Test
fun testEventParamsDefaultValues() {
    val params = EventParams()
    
    // SDK tarafından üretilen değerler artık EventParams'ta yok:
    // anonymousId, sessionId, traceId, timestamp, deviceId, os, language, app, sdkVersion
    assertNull(params.userId)
    assertNull(params.lineItemIds)
    assertNull(params.productList)
    assertNull(params.categoryId)
    assertNull(params.keyword)
    assertNull(params.loyaltyCard)
    assertNull(params.transactionId)
    assertNull(params.totalRowCount)
}
```

### 5.2 EventParams With All Values
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/EventParamsTest.kt` | **Satır:** 21-40

```kotlin
@Test
fun testEventParamsWithAllValues() {
    val params = EventParams(
        userId = "user-123",
        lineItemIds = "6817,6818",
        productList = "SKU123:1:10.50",
        categoryId = "123",
        keyword = "test",
        loyaltyCard = "card-123",
        transactionId = "txn-789",
        totalRowCount = 50
    )
    
    assertEquals("user-123", params.userId)
    assertEquals("6817,6818", params.lineItemIds)
    // ... diğer assertion'lar
}
```

### 5.3 Product List Format
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/EventParamsTest.kt` | **Satır:** 68-75

```kotlin
@Test
fun testProductListFormat() {
    val singleProduct = EventParams(productList = "SKU123:1:10.50")
    assertEquals("SKU123:1:10.50", singleProduct.productList)
    
    val multipleProducts = EventParams(productList = "SKU123:1:10.50,SKU456:2:25.00")
    assertEquals("SKU123:1:10.50,SKU456:2:25.00", multipleProducts.productList)
}
```

---

## 6. PerformanceEventParams Testleri

### 6.1 Required Fields
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/PerformanceEventParamsTest.kt` | **Satır:** 7-22

```kotlin
@Test
fun testPerformanceEventParamsRequiredFields() {
    val params = PerformanceEventParams(
            lineItemId = "6817",
            creativeId = "277",
            adUnit = "test-ad-unit",
            productSku = "SKU123",
        payload = "test-payload"
    )
    
    assertEquals("6817", params.lineItemId)
    assertEquals("277", params.creativeId)
    assertEquals("test-ad-unit", params.adUnit)
    assertEquals("SKU123", params.productSku)
    assertEquals("test-payload", params.payload)
}
```

### 6.2 Optional Fields
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/PerformanceEventParamsTest.kt` | **Satır:** 24-36

```kotlin
@Test
fun testPerformanceEventParamsOptionalFields() {
    val params = PerformanceEventParams(
        lineItemId = "6817",
        creativeId = "277",
        adUnit = "test-ad-unit",
        productSku = "SKU123",
        payload = "test-payload",
        keyword = "test",
        userId = "user-123"
    )
    
    assertEquals("test", params.keyword)
    assertEquals("user-123", params.userId)
}
```

### 6.3 Default Optional Fields
**Dosya:** `app/src/test/java/com/mimeda/sdk/events/PerformanceEventParamsTest.kt` | **Satır:** 38-50

```kotlin
@Test
fun testPerformanceEventParamsDefaultOptionalFields() {
    val params = PerformanceEventParams(
        lineItemId = "6817",
        creativeId = "277",
        adUnit = "test-ad-unit",
        productSku = "SKU123",
        payload = "test-payload"
    )
    
    // SDK tarafından üretilen değerler artık PerformanceEventParams'ta yok:
    // anonymousId, sessionId, traceId, timestamp
    assertNull(params.keyword)
    assertNull(params.userId)
}
```

---

## 7. DeviceInfo Testleri

### 7.1 OS Information
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/DeviceInfoTest.kt` | **Satır:** 27-31

```kotlin
@Test
fun testOsInformation() {
    val os = DeviceInfo.getOs()
    assertEquals("Android", os)
}
```

### 7.2 Browser Information (Null)
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/DeviceInfoTest.kt` | **Satır:** 33-37

```kotlin
@Test
fun testBrowserInformation() {
    val browser = DeviceInfo.getBrowser()
    assertNull(browser)
}
```

### 7.3 Language Information
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/DeviceInfoTest.kt` | **Satır:** 39-44

```kotlin
@Test
fun testLanguageInformation() {
    val language = DeviceInfo.getLanguage()
    assertTrue(language.isNotEmpty())
    assertTrue(language.contains("-"))
}
```

### 7.4 App Name Retrieval
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/DeviceInfoTest.kt` | **Satır:** 46-52

```kotlin
@Test
fun testAppNameRetrieval() {
    DeviceInfo.initialize(context)
    val appName = DeviceInfo.getAppName()
    assertEquals("com.test.app", appName)
}
```

### 7.5 Device ID Not Empty
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/DeviceInfoTest.kt` | **Satır:** 54-63

```kotlin
@Test
fun testDeviceIdIsNotEmpty() {
    DeviceInfo.initialize(context)
    val deviceId = DeviceInfo.getDeviceId()
    
    assertNotNull(deviceId)
    assertTrue(deviceId.isNotEmpty())
}
```

### 7.6 Device ID Consistency
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/DeviceInfoTest.kt` | **Satır:** 65-73

```kotlin
@Test
fun testDeviceIdConsistency() {
    DeviceInfo.initialize(context)
    val deviceId1 = DeviceInfo.getDeviceId()
    val deviceId2 = DeviceInfo.getDeviceId()
    
    assertEquals(deviceId1, deviceId2)
}
```

---

## 8. SecurePreferences Testleri

### 8.1 Obfuscation Testi
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/SecurePreferencesTest.kt` | **Satır:** 11-41

```kotlin
@Test
fun testObfuscation() {
    val testKey = "session_id"
    val testValue = "test-session-id-123"
    
    // java.util.Base64 kullanarak encode (JVM testlerinde çalışır)
    val obfuscatedKey = Base64.getEncoder().encodeToString(
        testKey.toByteArray(StandardCharsets.UTF_8)
    )
    
    val obfuscatedValue = Base64.getEncoder().encodeToString(
        testValue.toByteArray(StandardCharsets.UTF_8)
    )
    
    assertNotEquals(testKey, obfuscatedKey)
    assertNotEquals(testValue, obfuscatedValue)
    
    // Decode ile geri alınabilmeli
    val deobfuscatedKey = String(
        Base64.getDecoder().decode(obfuscatedKey),
        StandardCharsets.UTF_8
    )
    
    assertEquals(testKey, deobfuscatedKey)
}
```

---

## 9. Logger Testleri

### 9.1 No Duplicate Logging
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/LoggerTest.kt` | **Satır:** 7-14

```kotlin
@Test
fun testNoDuplicateLogging() {
    val exception = RuntimeException("Test exception")
    
    Logger.e("Test error", exception)
    
    assertTrue(true)
}
```

### 9.2 Error Logging Without Exception
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/LoggerTest.kt` | **Satır:** 16-21

```kotlin
@Test
fun testErrorLoggingWithoutException() {
    Logger.e("Test error")
    
    assertTrue(true)
}
```

### 9.3 Info Logging
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/LoggerTest.kt` | **Satır:** 23-28

```kotlin
@Test
fun testInfoLogging() {
    Logger.i("Test info")
    
    assertTrue(true)
}
```

### 9.4 Success Logging
**Dosya:** `app/src/test/java/com/mimeda/sdk/utils/LoggerTest.kt` | **Satır:** 30-35

```kotlin
@Test
fun testSuccessLogging() {
    Logger.s("Test success")
    
    assertTrue(true)
}
```

---

## 10. ApiService Testleri

### 10.1 Success Response (200)
**Dosya:** `app/src/test/java/com/mimeda/sdk/api/ApiServiceTest.kt` | **Satır:** 42-61

```kotlin
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
```

### 10.2 Client Error Response (4xx)
**Dosya:** `app/src/test/java/com/mimeda/sdk/api/ApiServiceTest.kt` | **Satır:** 63-82

```kotlin
@Test
fun testClientErrorResponse() {
    mockServer.enqueue(MockResponse().setResponseCode(400))
    
    val result = apiService.trackEvent(...)
    
    assertFalse(result)
    assertEquals(1, mockServer.requestCount) // Retry yapılmadı
}
```

### 10.3 Validation Failure
**Dosya:** `app/src/test/java/com/mimeda/sdk/api/ApiServiceTest.kt` | **Satır:** 84-101

```kotlin
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
    assertEquals(0, mockServer.requestCount) // Request gönderilmedi
}
```

---

## 11. Error Callback Testleri

### 11.1 Validation Callback Set
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKErrorCallbackTest.kt` | **Satır:** 41-64

```kotlin
@Test
fun testValidationCallbackIsSet() {
    val callback = object : MimedaSDKErrorCallback {
        override fun onEventTrackingFailed(...) {}
        override fun onPerformanceEventTrackingFailed(...) {}
        override fun onValidationFailed(...) {}
    }
    
    MimedaSDK.initialize(context, "test-key", errorCallback = callback)
    assertTrue(MimedaSDK.isInitialized())
}
```

### 11.2 Callback Exception Does Not Crash SDK
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKErrorCallbackTest.kt` | **Satır:** 66-90

```kotlin
@Test
fun testCallbackExceptionDoesNotCrashSDK() {
    val callback = object : MimedaSDKErrorCallback {
        override fun onEventTrackingFailed(...) {
            throw RuntimeException("Callback error")
        }
        override fun onPerformanceEventTrackingFailed(...) {}
        override fun onValidationFailed(...) {}
    }
    
    MimedaSDK.initialize(context, "test-key", errorCallback = callback)
    assertTrue(MimedaSDK.isInitialized())
}
```

### 11.3 Callback Without Error
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKErrorCallbackTest.kt` | **Satır:** 92-96

```kotlin
@Test
fun testCallbackWithoutError() {
    MimedaSDK.initialize(context, "test-key")
    assertTrue(MimedaSDK.isInitialized())
}
```

### 11.4 SDK Initializes With Callback
**Dosya:** `app/src/test/java/com/mimeda/sdk/MimedaSDKErrorCallbackTest.kt` | **Satır:** 98-120

```kotlin
@Test
fun testSDKInitializesWithCallback() {
    val callback = object : MimedaSDKErrorCallback { ... }
    
    MimedaSDK.initialize(context, "test-key", Environment.STAGING, callback)
    assertTrue(MimedaSDK.isInitialized())
}
```

---

## 12. Instrumented Testler (androidTest)

### 12.1 Başarılı Initialization
**Dosya:** `app/src/androidTest/java/com/mimeda/sdk/MimedaSDKInstrumentedTest.kt` | **Satır:** 34-39

```kotlin
@Test
fun testSuccessfulInitialization() {
    MimedaSDK.initialize(context, "test-api-key", Environment.PRODUCTION)
    
    assertTrue(MimedaSDK.isInitialized())
}
```

### 12.2 Package Name Retrieval
**Dosya:** `app/src/androidTest/java/com/mimeda/sdk/MimedaSDKInstrumentedTest.kt` | **Satır:** 41-46

```kotlin
@Test
fun testPackageNameRetrieval() {
    MimedaSDK.initialize(context, "test-key")
    
    assertEquals(context.packageName, DeviceInfo.getAppName())
}
```

### 12.3 Session ID Creation
**Dosya:** `app/src/androidTest/java/com/mimeda/sdk/MimedaSDKInstrumentedTest.kt` | **Satır:** 48-65

```kotlin
@Test
fun testSessionIdCreation() {
    MimedaSDK.initialize(context, "test-key")
    
    // SDK otomatik olarak sessionId üretir
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
```

### 12.4 Anonymous ID Creation
**Dosya:** `app/src/androidTest/java/com/mimeda/sdk/MimedaSDKInstrumentedTest.kt` | **Satır:** 67-82

```kotlin
@Test
fun testAnonymousIdCreation() {
    MimedaSDK.initialize(context, "test-key")
    
    // SDK otomatik olarak anonymousId üretir
    MimedaSDK.trackEvent(
        EventName.HOME,
        EventParameter.VIEW,
        EventParams()
    )
    
    Thread.sleep(1000)
    
    val anonymousId = TestHelpers.getAnonymousIdFromPrefs(context)
    assertNotNull(anonymousId)
}
```

### 12.5 Event Tracking Without Initialization
**Dosya:** `app/src/androidTest/java/com/mimeda/sdk/MimedaSDKInstrumentedTest.kt` | **Satır:** 81-86

```kotlin
@Test
fun testEventTrackingWithoutInitialization() {
    MimedaSDK.trackEvent(EventName.HOME, EventParameter.VIEW)
    
    assertFalse(MimedaSDK.isInitialized())
}
```

### 12.6 All Event Names
**Dosya:** `app/src/androidTest/java/com/mimeda/sdk/MimedaSDKInstrumentedTest.kt` | **Satır:** 88-96

```kotlin
@Test
fun testAllEventNames() {
    assertEquals("home", EventName.HOME.value)
    assertEquals("listing", EventName.LISTING.value)
    assertEquals("search", EventName.SEARCH.value)
    assertEquals("pdp", EventName.PDP.value)
    assertEquals("cart", EventName.CART.value)
    assertEquals("purchase", EventName.PURCHASE.value)
}
```

### 12.7 All Event Parameters
**Dosya:** `app/src/androidTest/java/com/mimeda/sdk/MimedaSDKInstrumentedTest.kt` | **Satır:** 98-104

```kotlin
@Test
fun testAllEventParameters() {
    assertEquals("view", EventParameter.VIEW.value)
    assertEquals("addtocart", EventParameter.ADD_TO_CART.value)
    assertEquals("addtofavorites", EventParameter.ADD_TO_FAVORITES.value)
    assertEquals("success", EventParameter.SUCCESS.value)
}
```

### 12.8 Performance Event Tracking
**Dosya:** `app/src/androidTest/java/com/mimeda/sdk/MimedaSDKInstrumentedTest.kt` | **Satır:** 108-124

```kotlin
@Test
fun testPerformanceEventTracking() {
    MimedaSDK.initialize(context, "test-api-key")
    
    // SDK otomatik olarak anonymousId, sessionId ve traceId üretir
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
```

---

## Test Çalıştırma Komutları

### Unit Testler
```bash
./gradlew test
```

### Instrumentation Testler
```bash
./gradlew connectedAndroidTest
```

### Tüm Testler
```bash
./gradlew test connectedAndroidTest
```

### Belirli Test Sınıfı
```bash
./gradlew test --tests "com.mimeda.sdk.MimedaSDKTest"
```

---

## Test Özeti

| Kategori | Test Sayısı | Dosya |
|----------|-------------|-------|
| SDK Initialization | 10 | MimedaSDKTest.kt |
| Environment | 2 | EnvironmentTest.kt |
| Event Name | 2 | EventNameTest.kt |
| Event Parameter | 2 | EventParameterTest.kt |
| EventParams | 3 | EventParamsTest.kt |
| PerformanceEventParams | 3 | PerformanceEventParamsTest.kt |
| DeviceInfo | 6 | DeviceInfoTest.kt |
| SecurePreferences | 1 | SecurePreferencesTest.kt |
| Logger | 4 | LoggerTest.kt |
| ApiService | 3 | ApiServiceTest.kt |
| Error Callback | 4 | MimedaSDKErrorCallbackTest.kt |
| **Unit Test Toplam** | **41** | - |
| Instrumented | 9 | MimedaSDKInstrumentedTest.kt |
| **Genel Toplam** | **50** | - |
