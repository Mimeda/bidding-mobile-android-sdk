# Mimeda Android SDK

Mimeda Android SDK, Android uygulamalarınıza kolayca entegre edebileceğiniz bir event tracking kütüphanesidir. SDK, event'leri arka planda HTTP API çağrıları ile Mimeda sistemine iletir.

## Özellikler

- ✅ Kolay entegrasyon
- ✅ Thread-safe event tracking
- ✅ Hata yönetimi (SDK hiçbir zaman ana uygulamayı crash etmez)
- ✅ Background thread'de çalışır
- ✅ Minimum bağımlılık

## Kurulum

### Gradle (Maven/JitPack)

Projenizin `build.gradle` (veya `build.gradle.kts`) dosyasına ekleyin:

```groovy
dependencies {
    implementation 'com.mimeda:sdk:1.0.0'
}
```

**Not:** SDK henüz yayınlanmadıysa, yerel olarak kullanabilir veya JitPack üzerinden yayınlayabilirsiniz.

### Yerel Kullanım (Geliştirme Aşamasında)

1. Bu projeyi klonlayın
2. `settings.gradle.kts` dosyanıza ekleyin:
```kotlin
include(":mimeda-sdk")
project(":mimeda-sdk").projectDir = File("../mimeda-android-sdk/app")
```

## Kullanım

### 1. SDK'yı Başlatma

Uygulamanızın `Application` sınıfında veya ilk Activity'nizde SDK'yı başlatın:

```kotlin
import com.mimeda.sdk.MimedaSDK

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // SDK'yı başlat (package name otomatik olarak context'ten alınır)
        MimedaSDK.initialize(this, "your-api-key-here")
    }
}
```

**Önemli:** 
- `AndroidManifest.xml` dosyanızda `Application` sınıfınızı tanımlamayı unutmayın
- Package name otomatik olarak `context.packageName`'den alınır (geliştirici tarafından verilmez)
- Her API isteğinde hem API key hem de package name gönderilir
- Backend'de API key + package name kombinasyonu doğrulanır (güvenlik için)

**Cleanup (Opsiyonel):**
Uygulama kapanırken SDK'yı temizlemek için `shutdown()` metodunu çağırabilirsiniz:
```kotlin
override fun onTerminate() {
    super.onTerminate()
    MimedaSDK.shutdown() // ExecutorService'i düzgün şekilde kapatır
}
```

```xml
<application
    android:name=".MyApplication"
    ...>
</application>
```

### 2. Event Tracking

Herhangi bir yerden event tracking yapabilirsiniz:

```kotlin
import com.mimeda.sdk.MimedaSDK

// Basit event
MimedaSDK.trackEvent("button_clicked")

// Parametreli event
MimedaSDK.trackEvent(
    "purchase_completed",
    mapOf(
        "product_id" to "12345",
        "price" to 99.99,
        "currency" to "USD"
    )
)
```

### 3. Örnek Kullanım Senaryoları

#### Button Click Event
```kotlin
button.setOnClickListener {
    MimedaSDK.trackEvent("button_clicked", mapOf(
        "button_id" to "login_button",
        "screen" to "login"
    ))
    // Button click logic...
}
```

#### Screen View Event
```kotlin
override fun onResume() {
    super.onResume()
    MimedaSDK.trackEvent("screen_viewed", mapOf(
        "screen_name" to "HomeActivity"
    ))
}
```

#### User Action Event
```kotlin
fun onUserLogin(userId: String) {
    MimedaSDK.trackEvent("user_logged_in", mapOf(
        "user_id" to userId,
        "timestamp" to System.currentTimeMillis()
    ))
}
```

## Yapılandırma (Configuration)

SDK, .NET'teki `appsettings.json` benzeri bir yapılandırma sistemi kullanır. Tüm sabit değerler `gradle.properties` veya `local.properties` dosyasından yönetilir.

### Yapılandırma Değerleri

SDK aşağıdaki yapılandırma değerlerini destekler:

| Özellik | Varsayılan | Açıklama |
|---------|-----------|----------|
| `MIMEDA_API_BASE_URL` | `https://api.mimeda.com` | API endpoint base URL |
| `MIMEDA_CONNECT_TIMEOUT` | `10` | Bağlantı timeout (saniye) |
| `MIMEDA_READ_TIMEOUT` | `30` | Okuma timeout (saniye) |
| `MIMEDA_WRITE_TIMEOUT` | `30` | Yazma timeout (saniye) |
| `MIMEDA_DEBUG_LOGGING` | `false` | Debug logging aktif/pasif |

### Yapılandırma Yöntemleri

#### 1. gradle.properties (Proje Geneli)

`gradle.properties` dosyasına ekleyin:

```properties
MIMEDA_API_BASE_URL=https://api-staging.mimeda.com
MIMEDA_CONNECT_TIMEOUT=15
MIMEDA_DEBUG_LOGGING=true
```

#### 2. local.properties (Yerel Override - Önerilen)

`local.properties` dosyasına ekleyin (bu dosya git'e eklenmez):

```properties
MIMEDA_API_BASE_URL=https://api-dev.mimeda.com
MIMEDA_DEBUG_LOGGING=true
```

**Not:** `local.properties` dosyası `.gitignore`'da olduğu için git'e eklenmez. Her geliştirici kendi local yapılandırmasını yapabilir.

#### 3. Build Variant'a Göre Farklı Değerler

`app/build.gradle.kts` dosyasında build type'lara göre farklı değerler tanımlayabilirsiniz:

```kotlin
buildTypes {
    debug {
        // Debug için farklı endpoint
    }
    release {
        // Production endpoint
    }
}
```

### Yapılandırma Nasıl Çalışır?

1. **BuildConfig**: Gradle build sırasında `BuildConfig` sınıfı oluşturulur
2. **Property Resolution**: `gradle.properties` → `local.properties` → Varsayılan değerler
3. **Compile-time**: Değerler compile-time'da BuildConfig'e eklenir
4. **Runtime**: SDK, BuildConfig'den değerleri okur

### Örnek: Staging Ortamı İçin Yapılandırma

`local.properties` dosyası:

```properties
MIMEDA_API_BASE_URL=https://api-staging.mimeda.com
MIMEDA_DEBUG_LOGGING=true
MIMEDA_CONNECT_TIMEOUT=20
```

Bu yapılandırma ile SDK, staging ortamına event gönderir ve debug logları aktif olur.

## Gereksinimler

- Minimum SDK: 24 (Android 7.0)
- Target SDK: 36
- Kotlin
- Internet Permission (otomatik olarak eklenir)

## Bağımlılıklar

SDK aşağıdaki kütüphaneleri kullanır:

- OkHttp 4.12.0 (HTTP client)
- Gson 2.10.1 (JSON parsing)
- AndroidX Core KTX 1.17.0

## Güvenlik

SDK, güvenlik için aşağıdaki önlemleri alır:

### Reverse Engineering Koruması

**SDK'yı Decompile Eden Biri Ne Görebilir?**
- ✅ Hangi header'ların gönderildiğini (`X-API-Key`, `X-Package-Name`, `X-Timestamp`)
- ✅ Endpoint URL'ini (`BuildConfig.API_BASE_URL`)
- ✅ Request formatını (JSON yapısı)
- ❌ API key'i göremez (runtime'da `initialize()` metoduna parametre olarak veriliyor)
- ❌ Package name'i göremez (runtime'da `context.packageName`'den alınıyor)

**Ancak Dikkat:**
- Eğer birisi SDK'yı kullanan uygulamayı decompile ederse, `MimedaSDK.initialize(context, "api-key")` çağrısında API key'i görebilir
- Bu durumda Postman'den istek atabilir, **AMA** backend'de API key + package name kombinasyonu kontrol edildiği için:
  - Eğer kombinasyon kayıtlı değilse → İstek reddedilir ✅
  - Eğer kombinasyon kayıtlıysa → İstek kabul edilir (bu normal, çünkü gerçek uygulama zaten bu API key'i kullanıyor)

### Package Name Doğrulaması

- Her API isteğinde hem `X-API-Key` hem de `X-Package-Name` header'ları gönderilir
- Backend'de API key oluşturulurken hangi package name ile kullanılacağı belirlenir
- Sadece kayıtlı package name'den gelen istekler kabul edilir
- **Önemli:** API key çalınsa bile, sadece kayıtlı package name ile çalışır

### Header'lar

Her API isteğinde gönderilen header'lar:
- `X-API-Key`: API anahtarı
- `X-Package-Name`: Uygulama paket adı (örn: `com.example.app`) - Otomatik olarak SDK tarafından eklenir
- `X-Timestamp`: İstek zamanı (milliseconds)
- `Content-Type`: `application/json`
- `Accept`: `application/json`

### Backend Doğrulama Örneği (.NET Core)

**ÖNEMLİ GÜVENLİK NOTU:** Secret key mekanizması güvenlik nedeniyle kaldırılmıştır. SDK içinde secret key saklamak güvenli değildir çünkü AAR dosyası reverse engineering ile açılabilir ve secret key görülebilir.

Güvenlik sadece **API key + package name kombinasyonu** ile sağlanır. Backend'de her istekte şu kontrolleri yapmalısınız:

```csharp
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

public class ValidateMimedaRequestAttribute : ActionFilterAttribute
{
    private const int TIMESTAMP_TOLERANCE_MS = 10000; // 10 saniye

    public override void OnActionExecuting(ActionExecutingContext context)
    {
        var request = context.HttpContext.Request;
        
        // Header'ları al
        if (!request.Headers.TryGetValue("X-API-Key", out var apiKey) ||
            !request.Headers.TryGetValue("X-Package-Name", out var packageName) ||
            !request.Headers.TryGetValue("X-Timestamp", out var timestampStr))
        {
            context.Result = new UnauthorizedObjectResult(new { error = "Missing required headers" });
            return;
        }

        // 1. Timestamp kontrolü (10 saniye içinde olmalı - replay attack önleme)
        if (!long.TryParse(timestampStr, out var timestamp))
        {
            context.Result = new UnauthorizedObjectResult(new { error = "Invalid timestamp" });
            return;
        }

        var currentTime = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
        if (Math.Abs(currentTime - timestamp) > TIMESTAMP_TOLERANCE_MS)
        {
            context.Result = new UnauthorizedObjectResult(new { error = "Request expired" });
            return;
        }

        // 2. API key + package name kombinasyonu kontrolü (ANA GÜVENLİK KONTROLÜ)
        if (!IsValidCombination(apiKey, packageName))
        {
            context.Result = new UnauthorizedObjectResult(new { error = "Invalid API key or package name" });
            return;
        }

        base.OnActionExecuting(context);
    }

    private bool IsValidCombination(string apiKey, string packageName)
    {
        // Veritabanında API key + package name kombinasyonunu kontrol et
        // Örnek implementasyon:
        // return _dbContext.ApiKeys.Any(k => k.Key == apiKey && k.PackageName == packageName);
        return true; // Placeholder - gerçek implementasyon gerekli
    }
}

// Controller'da kullanım:
[ApiController]
[Route("api/[controller]")]
public class EventsController : ControllerBase
{
    [HttpPost]
    [ValidateMimedaRequest] // Attribute ile doğrulama
    public IActionResult TrackEvent([FromBody] EventData eventData)
    {
        // Event'i işle
        return Ok(new { success = true });
    }
}
```

**Güvenlik Önerileri:**
- ✅ API key + package name kombinasyonunu veritabanında saklayın ve kontrol edin
- ✅ Timestamp kontrolü ile replay attack'leri önleyin (10 saniye içinde olmalı)
- ✅ Rate limiting ekleyin (çok fazla istek engelleme - örn: dakikada 100 istek)
- ✅ IP whitelisting (opsiyonel - sadece belirli IP'lerden istek kabul et)
- ✅ API key rotation (düzenli olarak API key'leri yenileyin)
- ✅ Anormal aktivite tespiti (aynı API key'den çok farklı package name'ler geliyorsa uyar)
- ❌ Secret key'i SDK içinde saklamayın (güvenlik açığı - AAR dosyası decompile edilebilir)
- ❌ API key'i kod içinde hardcode etmeyin (uygulama decompile edilebilir)

## Hata Yönetimi

SDK, tüm hataları içeride yakalar ve ana uygulamanızı etkilemez:

- Network hataları
- API hataları (4xx, 5xx) - Package name doğrulama hatası dahil
- JSON parsing hataları
- Diğer beklenmeyen hatalar

Tüm hatalar SDK içinde loglanır ancak exception fırlatılmaz.

## ProGuard

Release build'lerde ProGuard kullanıyorsanız, SDK otomatik olarak gerekli kuralları içerir. Ek bir yapılandırma gerekmez.

## Dokümantasyon

Detaylı teknik dokümantasyon için [TECH.md](TECH.md) dosyasına bakın.

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın.

## Destek

Sorularınız için: [destek@mimeda.com]

