# Mimeda Bidding Android SDK

[![Maven Central](https://img.shields.io/maven-central/v/tr.com.mimeda/bidding-mobile-android-sdk)](https://central.sonatype.com/artifact/tr.com.mimeda/bidding-mobile-android-sdk)
[![Min SDK](https://img.shields.io/badge/min%20SDK-24-blue)](https://developer.android.com/studio/releases/platforms)

Mimeda Android SDK, Mimeda bidding platformu için geliştirilmiş, event tracking ve performance monitoring özellikleri sunan hafif ve güvenli bir Android kütüphanesidir.

## İçindekiler

- [Özellikler](#özellikler)
- [Kurulum](#kurulum)
  - [Gradle (Kotlin DSL)](#gradle-kotlin-dsl)
  - [Gradle (Groovy)](#gradle-groovy)
  - [Snapshot Versiyonları (Staging)](#snapshot-versiyonları-staging)
  - [AndroidManifest İzinleri](#androidmanifest-izinleri)
  - [Kullanılan Kütüphaneler-bağımlılıklar](#kullanılan-kütüphaneler-bağımlılıklar)
- [Hızlı Başlangıç](#hızlı-başlangıç)
- [API Referansı](#api-referansı)
- [Debug Logging](#debug-logging)
- [ProGuard Kuralları](#proguard-kuralları)
- [Gereksinimler](#gereksinimler)
- [Güvenlik](#güvenlik)
- [Sorun Giderme](#sorun-giderme)
- [Gitflow ve CI/CD](#gitflow-ve-cicd)
- [Destek](#destek)
- [Sürüm Geçmişi](#-sürüm-geçmişi)

## Özellikler

- **Event Tracking**: Kullanıcı etkileşimlerini takip edin
- **Performance Monitoring**: Reklam performans metriklerini izleyin
- **Güvenli Depolama**: EncryptedSharedPreferences ile hassas verilerin güvenli saklanması
- **Input Sanitization**: Otomatik veri temizleme (XSS, HTML tag, SQL injection koruması)
- **Hafif ve Optimize Edilmiş**: ProGuard/R8 ile optimize edilmiş, minimal boyut
- **Automatic Retry**: Ağ hatalarında otomatik yeniden deneme
- **Debug Logging**: Geliştirme sırasında detaylı log desteği
- **Environment Support**: Production ve Staging ortamları desteği

## Kurulum

### Gradle (Kotlin DSL)

`build.gradle.kts` dosyanıza aşağıdaki dependency'yi ekleyin:

```kotlin
dependencies {
    implementation("tr.com.mimeda:bidding-mobile-android-sdk:1.0.0")
}
```

### Gradle (Groovy)

`build.gradle` dosyanıza aşağıdaki dependency'yi ekleyin:

```groovy
dependencies {
    implementation 'tr.com.mimeda:bidding-mobile-android-sdk:1.0.0'
}
```

**Not:** SDK, OkHttp ve Gson dependency'lerini transitif olarak sağlar. Bu kütüphaneleri ayrıca eklemenize gerek yoktur.

### AndroidManifest İzinleri

SDK'nın çalışabilmesi için yalnızca internet erişim izni gereklidir:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Bu izin, event ve performance isteklerinin Mimeda sunucularına gönderilebilmesi için zorunludur.

### Kullanılan Kütüphaneler (Bağımlılıklar)

SDK, aşağıdaki kütüphaneleri kullanır:

- **OkHttp 4.12.0**  
  - HTTP client olarak kullanılır, tüm http isteklerini yönetir.
- **OkHttp Logging Interceptor 4.12.0**  
  - Geliştirme sırasında HTTP request/response loglamak için kullanılır (debug amaçlı).
- **Gson 2.10.1**  
  - JSON serialization ve deserialization işlemleri için kullanılır.
- **AndroidX Core KTX 1.17.0**  
  - Android API'leri için Kotlin extension fonksiyonları sağlar.
- **AndroidX Security Crypto 1.1.0-alpha06**  
  - `EncryptedSharedPreferences` ile hassas verilerin (Session ID, Anonymous ID vb.) güvenli saklanmasını sağlar.

### Snapshot Versiyonları (Staging)

Staging ortamındaki beta versiyonları snapshot repository'den kullanabilirsiniz:

```kotlin
repositories {
    maven { 
        url = uri("https://central.sonatype.com/repository/maven-snapshots/") 
    }
    mavenCentral()
}

dependencies {
    implementation("tr.com.mimeda:bidding-mobile-android-sdk:1.0.0-beta.36-SNAPSHOT")
}
```

**Not:** Snapshot repository'ler doğrudan tarayıcıdan görüntülenemez. Snapshot'ların varlığını doğrulamak için Maven/Gradle ile dependency'yi çekmeyi deneyin.

## Hızlı Başlangıç

### 1. SDK'yı Başlatın

Uygulamanızın `Application` sınıfında veya `MainActivity`'de SDK'yı başlatın:

```kotlin
import com.mimeda.sdk.MimedaSDK
import com.mimeda.sdk.Environment

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        MimedaSDK.initialize(
            context = this,
            apiKey = "YOUR_API_KEY",
            environment = Environment.PRODUCTION // veya Environment.STAGING
        )
    }
}
```

### 2. Event Tracking

Kullanıcı etkileşimlerini takip edin:

```kotlin
import com.mimeda.sdk.MimedaSDK
import com.mimeda.sdk.events.EventName
import com.mimeda.sdk.events.EventParameter
import com.mimeda.sdk.events.EventParams

// Home / View - Ana sayfa görüntüleme
MimedaSDK.trackEvent(
    eventName = EventName.HOME,
    eventParameter = EventParameter.VIEW,
    params = EventParams()
)

// Home / AddtoCart - Ana sayfadan sepete ekleme
MimedaSDK.trackEvent(
    eventName = EventName.HOME,
    eventParameter = EventParameter.ADD_TO_CART,
    params = EventParams(
        lineItemIds = "item123",
        productList = "SKU456:1:99.99"
    )
)

// Home / AddtoFavorites - Ana sayfadan favorilere ekleme
MimedaSDK.trackEvent(
    eventName = EventName.HOME,
    eventParameter = EventParameter.ADD_TO_FAVORITES,
    params = EventParams(
        lineItemIds = "item123",
        productList = "SKU456:1:99.99"
    )
)

// Listing / View - Ürün listesi görüntüleme
MimedaSDK.trackEvent(
    eventName = EventName.LISTING,
    eventParameter = EventParameter.VIEW,
    params = EventParams(
        categoryId = "electronics",
        totalRowCount = 50
    )
)

// Listing / AddtoCart - Liste sayfasından sepete ekleme
MimedaSDK.trackEvent(
    eventName = EventName.LISTING,
    eventParameter = EventParameter.ADD_TO_CART,
    params = EventParams(
        categoryId = "electronics",
        lineItemIds = "item123",
        productList = "SKU456:1:99.99"
    )
)

// Listing / AddtoFavorites - Liste sayfasından favorilere ekleme
MimedaSDK.trackEvent(
    eventName = EventName.LISTING,
    eventParameter = EventParameter.ADD_TO_FAVORITES,
    params = EventParams(
        categoryId = "electronics",
        lineItemIds = "item123",
        productList = "SKU456:1:99.99"
    )
)

// Search / View - Arama sonuçları görüntüleme
MimedaSDK.trackEvent(
    eventName = EventName.SEARCH,
    eventParameter = EventParameter.VIEW,
    params = EventParams(
        keyword = "elektronik",
        categoryId = "electronics"
    )
)

// Search / AddtoCart - Arama sonuçlarından sepete ekleme
MimedaSDK.trackEvent(
    eventName = EventName.SEARCH,
    eventParameter = EventParameter.ADD_TO_CART,
    params = EventParams(
        keyword = "elektronik",
        lineItemIds = "item123",
        productList = "SKU456:1:99.99"
    )
)

// Search / AddtoFavorites - Arama sonuçlarından favorilere ekleme
MimedaSDK.trackEvent(
    eventName = EventName.SEARCH,
    eventParameter = EventParameter.ADD_TO_FAVORITES,
    params = EventParams(
        keyword = "elektronik",
        lineItemIds = "item123",
        productList = "SKU456:1:99.99"
    )
)

// Product Detail Page / View - Ürün detay sayfası görüntüleme
MimedaSDK.trackEvent(
    eventName = EventName.PDP,
    eventParameter = EventParameter.VIEW,
    params = EventParams(
        lineItemIds = "item123",
        productList = "SKU456:1:99.99"
    )
)

// Product Detail Page / AddtoCart - Ürün detay sayfasından sepete ekleme
MimedaSDK.trackEvent(
    eventName = EventName.PDP,
    eventParameter = EventParameter.ADD_TO_CART,
    params = EventParams(
        lineItemIds = "item123",
        productList = "SKU456:1:99.99"
    )
)

// Product Detail Page / AddtoFavorites - Ürün detay sayfasından favorilere ekleme
MimedaSDK.trackEvent(
    eventName = EventName.PDP,
    eventParameter = EventParameter.ADD_TO_FAVORITES,
    params = EventParams(
        lineItemIds = "item123",
        productList = "SKU456:1:99.99"
    )
)

// Cart / View - Sepet sayfası görüntüleme
MimedaSDK.trackEvent(
    eventName = EventName.CART,
    eventParameter = EventParameter.VIEW,
    params = EventParams(
        lineItemIds = "item123,item456",
        productList = "SKU456:1:99.99,SKU789:2:149.99"
    )
)

// Purchase / Success - Satın alma işlemi başarılı
MimedaSDK.trackEvent(
    eventName = EventName.PURCHASE,
    eventParameter = EventParameter.SUCCESS,
    params = EventParams(
        transactionId = "txn789",
        lineItemIds = "item123,item456",
        productList = "SKU456:1:99.99,SKU789:2:149.99"
    )
)
```

### 3. Performance Event Tracking

Reklam performans metriklerini takip edin:

```kotlin
import com.mimeda.sdk.MimedaSDK
import com.mimeda.sdk.events.PerformanceEventParams

// Impression (Görüntülenme)
MimedaSDK.trackPerformanceImpression(
    params = PerformanceEventParams(
        lineItemId = "line123",
        creativeId = "creative456",
        adUnit = "banner_top",
        productSku = "SKU789",
        payload = "custom_data",
        keyword = "electronics",
        userId = "user123"
    )
)

// Click (Tıklama)
MimedaSDK.trackPerformanceClick(
    params = PerformanceEventParams(
        lineItemId = "line123",
        creativeId = "creative456",
        adUnit = "banner_top",
        productSku = "SKU789",
        payload = "custom_data"
    )
)
```

## API Referansı

### MimedaSDK

Ana SDK sınıfı. Tüm işlemler bu singleton üzerinden yapılır. SDK yalnızca bir kez initialize edilmelidir. Tekrar initialize edilirse çağrı yok sayılır.

#### `initialize()`

SDK'yı başlatır.

```kotlin
fun initialize(
    context: Context,
    apiKey: String,
    environment: Environment = Environment.PRODUCTION,
    errorCallback: MimedaSDKErrorCallback? = null
)
```

**Parametreler:**
- `context`: Android Context (Application context önerilir)
- `apiKey`: Mimeda API anahtarı
- `environment`: `Environment.PRODUCTION` veya `Environment.STAGING`
- `errorCallback`: Hata durumlarında çağrılacak callback (opsiyonel)

#### `trackEvent()`

Event tracking için kullanılır.

```kotlin
fun trackEvent(
    eventName: EventName,
    eventParameter: EventParameter,
    params: EventParams = EventParams()
)
```

#### `trackPerformanceImpression()` / `trackPerformanceClick()`

Performance event tracking için kullanılır.

```kotlin
fun trackPerformanceImpression(params: PerformanceEventParams)
fun trackPerformanceClick(params: PerformanceEventParams)
```

#### `setDebugLogging()`

Debug loglarını açıp kapatır.

```kotlin
fun setDebugLogging(enabled: Boolean)
```

#### `isInitialized()`

SDK'nın başlatılıp başlatılmadığını kontrol eder.

```kotlin
fun isInitialized(): Boolean
```

#### `shutdown()`

SDK'yı kapatır ve kaynakları temizler.

```kotlin
fun shutdown()
```

### EventName

Kullanılabilir event isimleri:

- `HOME` - Ana sayfa
- `LISTING` - Ürün listesi
- `SEARCH` - Arama
- `PDP` - Ürün detay sayfası
- `CART` - Sepet
- `PURCHASE` - Satın alma

### EventParameter

Kullanılabilir event parametreleri:

- `VIEW` - Görüntüleme
- `ADD_TO_CART` - Sepete ekleme
- `ADD_TO_FAVORITES` - Favorilere ekleme
- `SUCCESS` - Başarılı işlem

### EventParams

Event parametreleri için data class:

```kotlin
data class EventParams(
    val userId: String? = null,
    val lineItemIds: String? = null,
    val productList: String? = null,
    val categoryId: String? = null,
    val keyword: String? = null,
    val loyaltyCard: String? = null,
    val transactionId: String? = null,
    val totalRowCount: Int? = null
)
```

### PerformanceEventParams

Performance event parametreleri için data class. Tüm alanlar opsiyoneldir, validasyon backend tarafında yapılmaktadır:

```kotlin
data class PerformanceEventParams(
    val lineItemId: String? = null,    // Opsiyonel
    val creativeId: String? = null,    // Opsiyonel
    val adUnit: String? = null,        // Opsiyonel
    val productSku: String? = null,    // Opsiyonel
    val payload: String? = null,       // Opsiyonel
    val keyword: String? = null,       // Opsiyonel
    val userId: String? = null         // Opsiyonel
)
```

### MimedaSDKErrorCallback

Hata durumlarını yakalamak için interface. Validasyon backend tarafında yapıldığı için SDK'da validasyon callback'i bulunmaz:

```kotlin
interface MimedaSDKErrorCallback {
    fun onEventTrackingFailed(
        eventName: EventName,
        eventParameter: EventParameter,
        error: Throwable
    )
    
    fun onPerformanceEventTrackingFailed(
        eventType: PerformanceEventType,
        error: Throwable
    )
}
```

**Kullanım örneği:**

```kotlin
MimedaSDK.initialize(
    context = this,
    apiKey = "YOUR_API_KEY",
    environment = Environment.PRODUCTION,
    errorCallback = object : MimedaSDKErrorCallback {
        override fun onEventTrackingFailed(
            eventName: EventName,
            eventParameter: EventParameter,
            error: Throwable
        ) {
            // Event tracking hatası (network hatası vb.)
            Log.e("MimedaSDK", "Event tracking failed: $eventName/$eventParameter", error)
        }
        
        override fun onPerformanceEventTrackingFailed(
            eventType: PerformanceEventType,
            error: Throwable
        ) {
            // Performance event tracking hatası (network hatası vb.)
            Log.e("MimedaSDK", "Performance event failed: $eventType", error)
        }
    }
)
```

## Debug Logging

Geliştirme sırasında debug loglarını açmak için:

```kotlin
MimedaSDK.setDebugLogging(true)
```

**Önemli Notlar:**
- Debug logging, release build'lerde de teknik olarak çalışabilir, ancak production ortamlarında kesinlikle açık bırakılmamalıdır. (eğer `setDebugLogging(true)` çağrılırsa)
- Production build'lerde varsayılan olarak kapalıdır
- Loglar `MimedaSDK` tag'i ile Logcat'te görüntülenir

## ProGuard Kuralları

SDK, ProGuard kurallarını otomatik olarak sağlar (`consumer-rules.pro`). Ek bir yapılandırma gerekmez. Ancak, eğer özel ProGuard kurallarınız varsa, SDK sınıflarını korumak için aşağıdaki kuralları ekleyebilirsiniz:

```proguard
# Mimeda SDK - Otomatik olarak AAR içinde gelir, eklemenize gerek yok
# -keep class com.mimeda.sdk.** { *; }
```

## Gereksinimler

- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 14+ (API 36+)
- **Kotlin:** 1.9.0+
- **Java:** 11+

## Güvenlik

SDK, aşağıdaki güvenlik özelliklerini içerir:

- **EncryptedSharedPreferences**: Hassas veriler AES-256-GCM ile şifrelenir
- **Input Sanitization**: Tüm kullanıcı girdileri otomatik olarak temizlenir (XSS, HTML tag, SQL injection koruması). Zorunlu alan validasyonu backend tarafında yapılmaktadır.
- **Secure Storage**: Session ID ve Anonymous ID güvenli bir şekilde saklanır (userId sdk tarafından üretilmediği için saklanmaz)
- **ProGuard Obfuscation**: Release build'lerde kod obfuscation aktif

## Thread Safety

- `MimedaSDK` bir Kotlin `object` olarak tasarlanmıştır ve singleton'dır.
- `isInitialized`, `eventTracker` ve `errorCallback` alanları `@Volatile` olarak işaretlenmiştir; bu sayede bir thread bu değişkenleri güncellediğinde, diğer tüm thread'ler hemen güncel değeri görür (görünürlük garantisi).
- `initialize()` metodu `@Synchronized` olarak tanımlıdır; aynı anda birden fazla thread'in SDK'yı başlatmasını engeller.(Race condition'ı önlemek için)
- Event gönderimi için kullanılan `EventTracker`, `Executors.newSingleThreadExecutor()` ile tek thread'li bir executor kullanır; bu sayede event işleme sırası korunur ve thread yönetimi basitleşir.

## Performance Considerations

- Event ve performance çağrıları, `EventTracker` içindeki tek thread'li executor üzerinden **asenkron** olarak çalıştırılır; bu sayede ana thread (UI thread) bloklanmaz.
- Her event, kendi HTTP isteği olarak gönderilir; şu anda batching yoktur. Bu, düşük hacimli kullanımda basitlik sağlar, yüksek hacimli kullanımda ise isteklere göre network trafiği artabilir.
- `ApiService` içinde `executeWithRetry` fonksiyonu ile **retry mekanizması** uygulanır:
  - Network hatalarında ve bazı timeout senaryolarında, maksimum `maxRetries` sayısına kadar yeniden deneme yapılır.
  - Yeniden denemeler arasında exponential backoff benzeri artan gecikme süresi kullanılır.
- Timeout ve retry süreleri `gradle.properties` üzerinden yapılandırılabilir (`MIMEDA_CONNECT_TIMEOUT`, `MIMEDA_READ_TIMEOUT`, `MIMEDA_WRITE_TIMEOUT`, `MIMEDA_MAX_RETRIES`, `MIMEDA_RETRY_BASE_DELAY_MS`).
- Tüm ağ istekleri OkHttp üzerinden, arka planda çalışan thread'lerde gerçekleştirilir; bu sayede UI performansı etkilenmez.

## Sorun Giderme

### SDK başlatılmadı hatası

```kotlin
if (!MimedaSDK.isInitialized()) {
    MimedaSDK.initialize(context, apiKey)
}
```

### Event tracking çalışmıyor

1. SDK'nın başlatıldığından emin olun
2. API key'in doğru olduğunu kontrol edin
3. Internet izninin verildiğinden emin olun
4. Debug logging'i açıp logları kontrol edin

### Loglar görünmüyor

```kotlin
// Debug logging'i açın
MimedaSDK.setDebugLogging(true)

// Logcat'te "MimedaSDK" tag'ini filtreleyin
```

## Gitflow ve CI/CD

Bu proje, GitHub Actions ile otomatik CI/CD pipeline'ı kullanır. Aşağıda branch stratejisi, PR süreci ve deployment akışı açıklanmaktadır.

### Branch Stratejisi

#### Staging Branch
- **Branch:** `staging`
- **Versiyon Formatı:** `1.0.0-beta.X-SNAPSHOT`
  - **X:** GitHub Actions run numarası (`github.run_number`) - her CI/CD çalıştırmasında otomatik artar
  - **Örnek:** `1.0.0-beta.36-SNAPSHOT` (36. CI/CD run'ı)
- **Deployment:** Snapshot repository'ye otomatik deploy
- **Repository:** `https://central.sonatype.com/repository/maven-snapshots/`

#### Production Branch
- **Branch:** `master` veya `main`
- **Versiyon:** `gradle.properties` dosyasındaki `MIMEDA_SDK_VERSION` değerinden okunur
- **Deployment:** Production repository'ye deploy (manual approval gerekli)
- **Repository:** Maven Central

**ÖNEMLİ:** Production'a deploy etmeden önce `gradle.properties` dosyasındaki `MIMEDA_SDK_VERSION` değerini manuel olarak artırmanız gerekir.

```properties
# gradle.properties
MIMEDA_SDK_VERSION=1.0.1  # Versiyonu artırın (örn: 1.0.0 → 1.0.1)
```

Versiyon artırma örnekleri (Semantic Versioning):
- Patch: `1.0.0` → `1.0.1` (bug fix)
- Minor: `1.0.0` → `1.1.0` (yeni özellik, geriye uyumlu)
- Major: `1.0.0` → `2.0.0` (breaking changes)

### PR Workflow

1. **PR Açma:**
   - PR'lar `main`, `master` veya `staging` branch'lerine açılmalıdır.
   - PR açıldığında otomatik olarak şu job'lar çalışır:
     - `build-and-test`: Proje build edilir, unit testler çalıştırılır, coverage raporu oluşturulur
     - `lint`: Kod kalitesi kontrolü yapılır

2. **PR Merge:**
   - PR merge edildiğinde (push event) `deploy` job'ı çalışır
   - Branch'e göre otomatik deployment yapılır

### CI/CD Pipeline

Pipeline üç ana job'dan oluşur:

#### 1. Build & Test
- Proje build edilir
- Unit testler çalıştırılır
- Test coverage raporu oluşturulur (minimum %60 coverage gerekir)
- PR'larda coverage raporu otomatik olarak PR'a yorum olarak eklenir

#### 2. Lint Check
- Android Lint çalıştırılır
- Kod kalitesi kontrolü yapılır
- Lint sonuçları artifact olarak saklanır

#### 3. Deploy
- Sadece push event'lerinde çalışır (PR'larda çalışmaz)
- `build-and-test` ve `lint` job'ları başarılı olursa çalışır
- Branch'e göre deployment yapılır:
  - `staging` → Snapshot repository
  - `master`/`main` → Production repository

### Deployment Akışı

#### Staging Deployment
1. `staging` branch'e push yapılır
2. CI/CD pipeline otomatik olarak çalışır
3. Versiyon otomatik oluşturulur: `1.0.0-beta.{run_number}-SNAPSHOT`
4. Snapshot repository'ye otomatik deploy edilir
5. Deploy sonrası snapshot versiyonu kullanılabilir

#### Production Deployment
1. `gradle.properties` dosyasındaki `MIMEDA_SDK_VERSION` değeri artırılır
2. Değişiklikler `master`/`main` branch'e merge edilir
3. CI/CD pipeline otomatik olarak çalışır
4. Production repository'ye bundle yüklenir
5. Sonatype Central Portal'da manual approval gerekir
6. Approval sonrası versiyon Maven Central'da yayınlanır

### Workflow Özeti

```
┌─────────────┐
│   PR Açma   │
└──────┬──────┘
       │
       ├─→ Build & Test
       ├─→ Lint Check
       │
       ▼
┌─────────────┐
│  PR Merge   │
└──────┬──────┘
       │
       ├─→ Staging Branch?
       │   └─→ Snapshot Deploy (Otomatik)
       │
       └─→ Master/Main Branch?
           └─→ Production Deploy (Manual Approval)
```

## Destek

- **Website:** [https://mimeda.com.tr](https://mimeda.com.tr)
- **Issues:** GitHub Issues üzerinden sorun bildirebilirsiniz

## 📝 Sürüm Geçmişi

Detaylı değişiklik listesi için [CHANGELOG.md](CHANGELOG.md) dosyasına bakın.

## Kaynaklar

- [Confluence](https://e-migros.atlassian.net/wiki/x/AQCK-g) - Süreç ile ilgili hazırlanan dokümantasyon
