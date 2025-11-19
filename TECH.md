# Mimeda Android SDK - Teknik Dokümantasyon

Bu dokümantasyon, Mimeda Android SDK'nın teknik detaylarını, kullanılan teknolojileri ve mimari kararları açıklar.

## İçindekiler

1. [Proje Yapısı](#proje-yapısı)
2. [Library Modülü vs Application Modülü](#library-modülü-vs-application-modülü)
3. [OkHttp - HTTP Client](#okhttp---http-client)
4. [Gson - JSON Parsing](#gson---json-parsing)
5. [ProGuard ve Code Obfuscation](#proguard-ve-code-obfuscation)
6. [Thread Safety ve Background Processing](#thread-safety-ve-background-processing)
7. [Error Handling Stratejisi](#error-handling-stratejisi)
8. [Singleton Pattern](#singleton-pattern)
9. [Gradle ve Bağımlılık Yönetimi](#gradle-ve-bağımlılık-yönetimi)

---

## Proje Yapısı

SDK, modüler bir yapıda organize edilmiştir:

```
app/src/main/java/com/mimeda/sdk/
├── MimedaSDK.kt              # Ana SDK sınıfı (public API)
├── api/
│   ├── ApiClient.kt          # HTTP client yapılandırması
│   └── ApiService.kt          # API endpoint çağrıları
├── events/
│   └── EventTracker.kt       # Event tracking logic
└── utils/
    └── Logger.kt             # Logging utility
```

### MimedaSDK.kt
Ana SDK sınıfı. Kullanıcıların etkileşimde bulunduğu tek public API'dir. Singleton pattern kullanır.

### ApiClient.kt
OkHttp client'ı yapılandırır. Timeout değerleri, interceptor'lar ve header'lar burada tanımlanır.

### ApiService.kt
HTTP isteklerini yönetir. Event'leri JSON formatında API'ye POST eder.

### EventTracker.kt
Event'leri background thread'de işler. ExecutorService kullanarak asenkron çalışır.

### Logger.kt
SDK içi logging için utility sınıfı. Production'da log'lar kapalıdır.

---

## Library Modülü vs Application Modülü

### Neden Library Modülü?

Android Studio'da iki tür modül vardır:

1. **Application Modülü**: Bağımsız çalışan bir uygulama
2. **Library Modülü**: Diğer uygulamalara entegre edilebilen kütüphane

SDK'mız bir **Library Modülü** olmalıdır çünkü:

- ✅ Bağımsız bir uygulama değil, kütüphane
- ✅ `.aar` (Android Archive) dosyası olarak derlenir
- ✅ `applicationId` gerektirmez
- ✅ Launcher icon, theme gibi UI bileşenlerine ihtiyaç duymaz
- ✅ Diğer projelere dependency olarak eklenebilir

### Dönüştürme İşlemi

Application modülünden library modülüne dönüştürme için:

1. `build.gradle.kts` dosyasında:
   ```kotlin
   // Önce
   plugins {
       alias(libs.plugins.android.application)
   }
   
   // Sonra
   plugins {
       alias(libs.plugins.android.library)
   }
   ```

2. `applicationId` satırını kaldırın (library'lerde olmaz)

3. Gereksiz resource dosyalarını temizleyin (launcher icons, themes)

---

## OkHttp - HTTP Client

### OkHttp Nedir?

OkHttp, Square tarafından geliştirilen modern bir HTTP client kütüphanesidir. Android'in varsayılan `HttpURLConnection`'ına göre çok daha güçlü ve esnektir.

### Neden OkHttp Kullandık?

1. **Performans**: Connection pooling, request/response caching
2. **Güvenilirlik**: Otomatik retry mekanizması
3. **Esneklik**: Interceptor'lar ile request/response manipülasyonu
4. **Modern API**: Kotlin coroutines desteği (gelecekte kullanılabilir)
5. **Yaygın Kullanım**: Android ekosisteminde standart

### OkHttp Yapılandırmamız

```kotlin
OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)    // Bağlantı timeout
    .readTimeout(30, TimeUnit.SECONDS)       // Okuma timeout
    .writeTimeout(30, TimeUnit.SECONDS)      // Yazma timeout
    .addInterceptor { chain ->               // API Key header ekleme
        // ...
    }
    .addInterceptor(loggingInterceptor)      // Debug logging
    .build()
```

### Timeout Değerleri

- **Connect Timeout (10s)**: Sunucuya bağlanma süresi
- **Read Timeout (30s)**: Response okuma süresi
- **Write Timeout (30s)**: Request yazma süresi

Bu değerler, mobil internet bağlantılarında yaşanabilecek gecikmeleri göz önünde bulundurarak seçilmiştir.

### Interceptor'lar

1. **API Key Interceptor**: Her request'e `X-API-Key` header'ı ekler
2. **Logging Interceptor**: Debug modunda request/response loglar

---

## Gson - JSON Parsing

### Gson Nedir?

Gson, Google tarafından geliştirilen bir JSON serialization/deserialization kütüphanesidir. Java/Kotlin objelerini JSON'a ve JSON'u objelere dönüştürür.

### Neden Gson Kullandık?

1. **Kolay Kullanım**: Minimal kod ile JSON işlemleri
2. **Performans**: Hızlı serialization/deserialization
3. **Esneklik**: Custom serializer/deserializer desteği
4. **Yaygın Kullanım**: Android ekosisteminde standart

### Kullanım Örneği

```kotlin
val gson = Gson()
val eventData = mapOf(
    "event_name" to "button_clicked",
    "params" to mapOf("button_id" to "login"),
    "timestamp" to System.currentTimeMillis()
)

val jsonBody = gson.toJson(eventData)  // Map -> JSON String
```

### Alternatifler

- **kotlinx.serialization**: Kotlin-native, ancak daha fazla yapılandırma gerektirir
- **Moshi**: Square'in Gson alternatifi, daha modern ancak daha az yaygın

Gson, basitlik ve yaygın kullanım nedeniyle tercih edilmiştir.

---

## ProGuard ve Code Obfuscation

### ProGuard Nedir?

ProGuard, Android uygulamaları için bir code obfuscation ve optimization aracıdır. Release build'lerde:

1. **Code Shrinking**: Kullanılmayan kodu kaldırır
2. **Obfuscation**: Sınıf ve metod isimlerini kısa, anlamsız isimlere dönüştürür
3. **Optimization**: Kodu optimize eder

### Neden ProGuard Kuralları Gerekli?

ProGuard, kod analizi yaparken reflection kullanan kütüphaneleri (OkHttp, Gson) tanımayabilir ve bu sınıfları silebilir veya obfuscate edebilir. Bu durumda runtime'da `ClassNotFoundException` veya `NoSuchMethodException` hataları oluşur.

### SDK'mızın ProGuard Kuralları

#### OkHttp Kuralları

```proguard
# OkHttp sınıflarını koru
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Uyarıları bastır (gerekli olmayan bağımlılıklar için)
-dontwarn okhttp3.**
-dontwarn okio.**
```

#### Gson Kuralları

```proguard
# Gson sınıflarını koru
-keep class com.google.gson.** { *; }

# Generic type bilgilerini koru (reflection için gerekli)
-keepattributes Signature
-keepattributes *Annotation*

# SerializedName annotation'larını koru
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
```

#### SDK Public API Kuralları

```proguard
# Public API'yi koru
-keep class com.mimeda.sdk.MimedaSDK { *; }
```

### R8 vs ProGuard

Modern Android build sistemleri R8 kullanır (ProGuard'ın geliştirilmiş versiyonu). R8, ProGuard kurallarını da destekler, bu yüzden aynı syntax kullanılır.

---

## Thread Safety ve Background Processing

### Neden Background Thread?

HTTP istekleri **asla** main (UI) thread'de yapılmamalıdır çünkü:

1. **ANR (Application Not Responding)**: Uzun süren işlemler UI'ı dondurur
2. **Kullanıcı Deneyimi**: Uygulama donuyor gibi görünür
3. **Android Kısıtlamaları**: Android 4.0+ main thread'de network işlemlerini engeller

### Çözümümüz: ExecutorService

```kotlin
private val executor = Executors.newSingleThreadExecutor()

fun track(eventName: String, params: Map<String, Any>) {
    executor.execute {
        // HTTP isteği burada yapılır
        apiService.trackEvent(eventName, params)
    }
}
```

### ExecutorService Avantajları

1. **Thread Pool**: Thread'leri yeniden kullanır (performans)
2. **Queue**: İstekler sıraya alınır
3. **Lifecycle Management**: Thread'lerin yaşam döngüsü yönetilir

### Alternatif: Kotlin Coroutines

Gelecekte Kotlin Coroutines'e geçilebilir:

```kotlin
// Örnek (şu an kullanılmıyor)
suspend fun trackEvent(eventName: String) {
    withContext(Dispatchers.IO) {
        apiService.trackEvent(eventName)
    }
}
```

Coroutines daha modern ancak minimum SDK 24 için ExecutorService yeterlidir.

---

## Error Handling Stratejisi

### Temel Prensip: SDK Asla Crash Etmemeli

SDK, ana uygulamayı **hiçbir koşulda** crash etmemelidir. Bu nedenle tüm kritik noktalarda try-catch kullanılır.

### Hata Senaryoları

1. **Network Hataları**
   - İnternet bağlantısı yok
   - Timeout
   - DNS hatası
   - **Çözüm**: Exception yakalanır, log yazılır, false döner

2. **API Hataları**
   - 4xx (Client Error)
   - 5xx (Server Error)
   - **Çözüm**: Response kontrol edilir, log yazılır, false döner

3. **JSON Hataları**
   - Serialization hatası
   - **Çözüm**: Exception yakalanır, log yazılır, false döner

4. **Diğer Beklenmeyen Hatalar**
   - Null pointer
   - Illegal state
   - **Çözüm**: Tüm exception'lar yakalanır

### Implementation

```kotlin
fun trackEvent(eventName: String, params: Map<String, Any>): Boolean {
    return try {
        // API çağrısı
        val response = client.newCall(request).execute()
        response.isSuccessful
    } catch (e: IOException) {
        Logger.e("Network error", e)
        false  // Exception fırlatılmaz
    } catch (e: Exception) {
        Logger.e("Unexpected error", e)
        false  // Exception fırlatılmaz
    }
}
```

### Kullanıcı Deneyimi

Kullanıcı, SDK'nın başarısız olması durumunda hiçbir şey fark etmez. Event tracking arka planda sessizce çalışır.

---

## Singleton Pattern

### Neden Singleton?

SDK, singleton pattern kullanır çünkü:

1. **Tek Instance**: Tüm uygulama boyunca tek bir SDK instance'ı olmalı
2. **Global Erişim**: Her yerden kolayca erişilebilir
3. **State Management**: Initialization state'i merkezi olarak yönetilir
4. **Resource Efficiency**: Gereksiz instance oluşturulmaz

### Implementation

```kotlin
object MimedaSDK {
    private var isInitialized = false
    private var eventTracker: EventTracker? = null
    
    fun initialize(context: Context, apiKey: String) {
        // ...
    }
    
    fun trackEvent(eventName: String, params: Map<String, Any>) {
        // ...
    }
}
```

### Kotlin `object` Keyword

Kotlin'de `object` keyword'ü singleton pattern'i otomatik olarak sağlar:

- Thread-safe initialization
- Lazy loading
- Tek instance garantisi

---

## Gradle ve Bağımlılık Yönetimi

### Version Catalog (libs.versions.toml)

Modern Android projeleri, bağımlılık versiyonlarını merkezi olarak yönetir:

```toml
[versions]
okhttp = "4.12.0"
gson = "2.10.1"

[libraries]
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
gson = { group = "com.google.code.gson", name = "gson", version.ref = "gson" }
```

### Avantajları

1. **Merkezi Yönetim**: Tüm versiyonlar tek yerde
2. **Tip Güvenliği**: IDE autocomplete desteği
3. **Kolay Güncelleme**: Tek yerden tüm versiyonlar güncellenir

### Bağımlılık Çakışmaları

SDK, minimum bağımlılık prensibini takip eder:

- Sadece gerekli kütüphaneler eklenir
- Versiyonlar güncel ve stabil seçilir
- Kullanıcı projesindeki bağımlılıklarla çakışma riski minimize edilir

### Transitive Dependencies

OkHttp ve Gson'un kendi bağımlılıkları vardır:

- **OkHttp** → Okio (I/O operations)
- **Gson** → Bağımlılık yok (standalone)

Bu bağımlılıklar otomatik olarak çözülür (Gradle tarafından).

---

## BuildConfig ve Yapılandırma Yönetimi

### BuildConfig Nedir?

BuildConfig, Android build sistemi tarafından otomatik olarak oluşturulan bir sınıftır. Compile-time'da sabit değerler içerir ve runtime'da değiştirilemez.

### Neden BuildConfig Kullandık?

.NET'teki `appsettings.json` benzeri bir yapılandırma sistemi için BuildConfig ideal bir çözümdür:

1. **Compile-time Safety**: Değerler compile-time'da kontrol edilir
2. **No Runtime Overhead**: Sabit değerler, performans kaybı olmadan kullanılır
3. **Build Variant Support**: Farklı build type'lar için farklı değerler
4. **Property Override**: `gradle.properties` veya `local.properties` ile override edilebilir

### Yapılandırma Hiyerarşisi

```
1. local.properties (en yüksek öncelik - git'e eklenmez)
   ↓
2. gradle.properties (proje geneli)
   ↓
3. build.gradle.kts defaultConfig (varsayılan değerler)
```

### Implementation

```kotlin
// build.gradle.kts
defaultConfig {
    val apiBaseUrl = project.findProperty("MIMEDA_API_BASE_URL") as String? 
        ?: "https://api.mimeda.com"
    
    buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
}

// Kod içinde kullanım
private val baseUrl = com.mimeda.sdk.BuildConfig.API_BASE_URL
```

### Avantajları

1. **Hardcoded Değerler Yok**: Tüm sabit değerler yapılandırma dosyalarından gelir
2. **Environment Support**: Dev, Staging, Production için farklı değerler
3. **Developer Friendly**: Her geliştirici kendi `local.properties` dosyasını kullanabilir
4. **CI/CD Friendly**: Build script'lerinde property override edilebilir

### Alternatifler

- **res/values/config.xml**: Runtime değerler için, ancak compile-time değil
- **SharedPreferences**: Runtime değerler için, ancak persistence gerektirir
- **Environment Variables**: CI/CD için, ancak local development için uygun değil

BuildConfig, compile-time sabit değerler için en uygun çözümdür.

---

## Sonuç

Mimeda Android SDK, modern Android geliştirme best practice'lerini takip ederek geliştirilmiştir:

- ✅ Modüler mimari
- ✅ Thread-safe implementation
- ✅ Comprehensive error handling
- ✅ Minimal bağımlılık
- ✅ Production-ready ProGuard kuralları
- ✅ Yapılandırma yönetimi (BuildConfig ile .NET appsettings.json benzeri)

SDK, kullanıcı uygulamalarını etkilemeden güvenilir bir şekilde event tracking yapar.

