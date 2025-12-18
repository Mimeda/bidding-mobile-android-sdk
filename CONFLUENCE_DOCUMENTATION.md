# Sıfırdan Android SDK Oluşturma Rehberi

Bu dokümantasyon, Mimeda Bidding Android SDK'nın sıfırdan nasıl oluşturulduğunu, yayınlandığını ve yönetildiğini detaylı bir şekilde açıklamaktadır.

---

## Panel ve Yönetimsel İşlemler

### 1. Central.Sonatype.com Hesap Açma Adımları

Maven Central'a yayın yapabilmek için öncelikle Sonatype Central Portal'da bir hesap oluşturmanız gerekmektedir.

**Adımlar:**
1. [Central.Sonatype.com](https://central.sonatype.com) adresine gidin
2. "Sign Up" veya "Sign In" butonuna tıklayın
3. GitHub, Google veya e-posta ile giriş yapın
4. Hesap bilgilerinizi tamamlayın ve e-posta doğrulamasını yapın
5. Hesabınız aktif hale geldikten sonra "Publisher" bölümüne geçin

**Önemli Notlar:**
- Central Portal, Maven Central'ın yeni yönetim arayüzüdür
- Hesap açma işlemi genellikle anında tamamlanır
- Publisher hesabı açmak için kurumsal bilgiler gerekebilir

---

### 2. Central.Sonatype.com'da Namespace Ekleme ve Validasyon İşlemleri

Maven Central'da yayın yapabilmek için bir namespace (groupId) kaydetmeniz ve doğrulamanız gerekmektedir.

**Namespace Kayıt Adımları:**
1. Central Portal'da "Publisher" bölümüne gidin
2. "Add Namespace" veya "Register Namespace" butonuna tıklayın
3. Namespace'inizi girin (örn: `tr.com.mimeda`)
4. Namespace tipini seçin:
   - **Domain Namespace**: Kendi domain'iniz varsa (örn: `com.mimeda`)
   - **GitHub Namespace**: GitHub kullanıcı adınız (örn: `io.github.mimeda`)
   - **Central Portal Namespace**: Central Portal tarafından sağlanan namespace

**Validasyon İşlemleri:**

Domain Namespace için:
- DNS TXT kaydı eklemeniz gerekir
- Central Portal size bir TXT kaydı verir
- DNS ayarlarınızda bu TXT kaydını ekleyin
- Validasyon genellikle birkaç dakika içinde tamamlanır

GitHub Namespace için:
- GitHub repository'nizin sahibi olduğunuzu doğrulamanız gerekir
- Central Portal, GitHub'da bir repository oluşturmanızı isteyebilir

**Örnek Namespace:**
- `tr.com.mimeda` - Domain bazlı namespace
- Validasyon sonrası bu namespace altında istediğiniz artifact'ları yayınlayabilirsiniz

**Validasyon Kontrolü:**
- Central Portal'da namespace'inizin durumunu kontrol edebilirsiniz
- "Verified" durumuna geçtiğinde yayın yapmaya başlayabilirsiniz

---

### 3. GPG Key Oluşturma ve Yönetimi

Maven Central'da yayın yapmak için tüm artifact'larınızı GPG ile imzalamanız gerekmektedir.

#### 3.1. GPG Key Oluşturma (CLI)

**Public ve Private Key Oluşturma:**

```bash
# GPG key oluşturma (interaktif)
gpg --full-generate-key

# Veya non-interaktif (script için)
gpg --batch --gen-key <<EOF
%no-protection
Key-Type: RSA
Key-Length: 4096
Subkey-Type: RSA
Subkey-Length: 4096
Name-Real: Mimeda Team
Name-Email: sdk@mimeda.com.tr
Expire-Date: 0
EOF
```

**Key ID'yi Bulma:**

```bash
# Key ID'yi listele
gpg --list-secret-keys --keyid-format LONG

# Örnek çıktı:
# sec   rsa4096/667146E8FFF98B03D8CB44C42B43628512829014 2024-01-01 [SC]
#        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
#        Bu kısım SIGNING_KEY_ID'dir
```

**SIGNING_KEY_ID:**
- Örnek: `667146E8FFF98B03D8CB44C42B43628512829014`
- Bu ID'yi GitHub Secrets'a ekleyeceksiniz

#### 3.2. Public Key'i Sunucuya Gönderme

Public key'inizi OpenPGP key server'larına yüklemelisiniz:

```bash
# Public key'i export et
gpg --armor --export 667146E8FFF98B03D8CB44C42B43628512829014 > public-key.asc

# Key'i keys.openpgp.org'a yükle
gpg --keyserver keys.openpgp.org --send-keys 667146E8FFF98B03D8CB44C42B43628512829014

# Alternatif olarak keyserver.ubuntu.com'a da yükleyebilirsiniz
gpg --keyserver keyserver.ubuntu.com --send-keys 667146E8FFF98B03D8CB44C42B43628512829014
```

**Key Doğrulama:**
- Key'inizi [keys.openpgp.org](https://keys.openpgp.org/search?q=667146E8FFF98B03D8CB44C42B43628512829014) adresinde arayarak doğrulayabilirsiniz
- Key'iniz listede görünüyorsa başarılıdır

#### 3.3. Private Key'i GitHub Secrets'a Ekleme

CI/CD pipeline'ında kullanmak için private key'i base64 formatında GitHub Secrets'a eklemeniz gerekmektedir:

```bash
# Private key'i export et (base64 formatında)
gpg --armor --export-secret-keys 667146E8FFF98B03D8CB44C42B43628512829014 | base64 > private-key-base64.txt

# Veya tek satırda
gpg --armor --export-secret-keys 667146E8FFF98B03D8CB44C42B43628512829014 | base64 -w 0
```

**GitHub Secrets'a Ekleme:**
1. GitHub repository'nize gidin
2. Settings → Secrets and variables → Actions
3. "New repository secret" butonuna tıklayın
4. Name: `SIGNING_KEY`
5. Value: Base64 encode edilmiş private key'i yapıştırın
6. "Add secret" butonuna tıklayın

**Diğer GPG Secrets:**
- `SIGNING_KEY_ID`: Key ID (örn: `667146E8FFF98B03D8CB44C42B43628512829014`)
- `SIGNING_PASSWORD`: GPG key oluştururken belirlediğiniz şifre (eğer şifre koyduysanız)

---

### 4. Central.Sonatype.com/UserToken'da Token Üretme

Maven Central'a yayın yapmak için authentication token'ı oluşturmanız gerekmektedir.

**Token Oluşturma Adımları:**
1. [Central.Sonatype.com](https://central.sonatype.com) adresine gidin
2. Giriş yapın
3. Profil ayarlarınıza gidin (sağ üst köşedeki profil ikonuna tıklayın)
4. "User Token" veya "Access Tokens" bölümüne gidin
5. "Generate Token" veya "Create Token" butonuna tıklayın
6. Token için bir isim verin (örn: "CI/CD Pipeline")
7. Token oluşturulduktan sonra **hemen kopyalayın** (bir daha gösterilmeyecek!)

**Token Bilgileri:**
- Token iki parçadan oluşur: `username` ve `password`
- Bu bilgileri GitHub Secrets'a ekleyeceksiniz

**Örnek Token Formatı:**
```
Username: abc123def456
Password: xyz789uvw012
```

**Önemli Notlar:**
- Token'ı güvenli bir yerde saklayın
- Token'ı bir kez görebilirsiniz, kaybetmeyin
- Token'ı GitHub Secrets'a eklemeden önce doğru kopyaladığınızdan emin olun

---

### 5. GitHub Secrets Yapılandırması

CI/CD pipeline'ının çalışabilmesi için aşağıdaki secrets'ları GitHub repository'nize eklemeniz gerekmektedir.

**GitHub Secrets Ekleme:**
1. Repository'nize gidin: `https://github.com/Mimeda/bidding-mobile-android-sdk`
2. Settings → Secrets and variables → Actions
3. "New repository secret" butonuna tıklayın
4. Aşağıdaki secrets'ları ekleyin:

#### 5.1. OSSRH_USERNAME
- **Name:** `OSSRH_USERNAME`
- **Value:** Central Portal'dan aldığınız token'ın username kısmı
- **Açıklama:** Sonatype Central Portal authentication username

#### 5.2. OSSRH_PASSWORD
- **Name:** `OSSRH_PASSWORD`
- **Value:** Central Portal'dan aldığınız token'ın password kısmı
- **Açıklama:** Sonatype Central Portal authentication password

#### 5.3. SIGNING_KEY
- **Name:** `SIGNING_KEY`
- **Value:** Base64 encode edilmiş GPG private key
- **Açıklama:** Artifact'ları imzalamak için kullanılan GPG private key (base64 formatında)

#### 5.4. SIGNING_KEY_ID
- **Name:** `SIGNING_KEY_ID`
- **Value:** GPG key ID (örn: `667146E8FFF98B03D8CB44C42B43628512829014`)
- **Açıklama:** GPG key'inizin ID'si

#### 5.5. SIGNING_PASSWORD
- **Name:** `SIGNING_PASSWORD`
- **Value:** GPG key oluştururken belirlediğiniz şifre (eğer şifre koyduysanız)
- **Açıklama:** GPG key'inizin şifresi (opsiyonel, eğer key şifreliyse)

**Secrets Kontrol Listesi:**
- [ ] OSSRH_USERNAME eklendi
- [ ] OSSRH_PASSWORD eklendi
- [ ] SIGNING_KEY eklendi (base64 formatında)
- [ ] SIGNING_KEY_ID eklendi
- [ ] SIGNING_PASSWORD eklendi (eğer key şifreliyse)

**Secrets Erişim URL'si:**
```
https://github.com/Mimeda/bidding-mobile-android-sdk/settings/secrets/actions
```

---

## SDK Hakkında Bilinmesi Gerekenler

### Mimeda Bidding Android SDK Genel Bakış

Mimeda Bidding Android SDK, Mimeda bidding platformu için geliştirilmiş, event tracking ve performance monitoring özellikleri sunan hafif ve güvenli bir Android kütüphanesidir.

**Temel Özellikler:**
- Event Tracking: Kullanıcı etkileşimlerini takip etme
- Performance Monitoring: Reklam performans metriklerini izleme
- Güvenli Depolama: EncryptedSharedPreferences ile hassas verilerin güvenli saklanması
- Input Validation: Otomatik veri doğrulama ve sanitization
- Automatic Retry: Ağ hatalarında otomatik yeniden deneme
- Thread Safety: Thread-safe singleton yapısı

---

### SDK Mimarisi ve Bileşenler

#### 1. MimedaSDK (Ana Sınıf)

SDK'nın ana giriş noktasıdır. Singleton pattern kullanılarak tasarlanmıştır.

**Özellikler:**
- `@Volatile` annotation'ları ile thread-safe değişkenler
- `@Synchronized` initialize metodu ile race condition koruması
- Lazy initialization pattern

**Temel Metodlar:**
```kotlin
// SDK'yı başlatma
MimedaSDK.initialize(
    context: Context,
    apiKey: String,
    environment: Environment,
    errorCallback: MimedaSDKErrorCallback?
)

// Event tracking
MimedaSDK.trackEvent(
    eventName: EventName,
    eventParameter: EventParameter,
    params: EventParams
)

// Performance tracking
MimedaSDK.trackPerformanceImpression(params: PerformanceEventParams)
MimedaSDK.trackPerformanceClick(params: PerformanceEventParams)
```

#### 2. EventTracker

Event'leri yöneten ve API'ye gönderen sınıftır.

**Özellikler:**
- Single-threaded executor kullanarak event sıralamasını korur
- Session ID yönetimi (30 dakika geçerlilik süresi)
- Anonymous ID yönetimi (cihaz bazlı, kalıcı)
- Input validation ve sanitization
- Asenkron event gönderimi (UI thread'i bloklamaz)

**Session Yönetimi:**
- Session ID, 30 dakika boyunca geçerlidir
- 30 dakika boyunca etkileşim olmazsa yeni session ID oluşturulur
- Session ID, EncryptedSharedPreferences'te güvenli bir şekilde saklanır

**Anonymous ID Yönetimi:**
- Cihaz bazlıdır ve uygulama silinmedikçe aynı kalır
- UUID formatında oluşturulur
- EncryptedSharedPreferences'te güvenli bir şekilde saklanır

#### 3. ApiService

HTTP isteklerini yöneten ve API ile iletişim kuran sınıftır.

**Özellikler:**
- Automatic Retry (exponential backoff)
- Environment bazlı URL yönetimi (Production/Staging)
- Query parameter validation
- Error handling ve callback desteği

**Automatic Retry:**
- Maksimum retry sayısı: `MAX_RETRIES` (varsayılan: 3)
- Retry delay: Exponential backoff (base delay: `RETRY_BASE_DELAY_MS`, varsayılan: 1000ms)
- Retry durumları: Network hataları, timeout'lar, 5xx server hataları
- Retry yapılmayan durumlar: 4xx client hataları

**Environment Yönetimi:**
- **Production:**
  - Event URL: `https://event.mlink.com.tr`
  - Performance URL: `https://performance.mlink.com.tr`
- **Staging:**
  - Event URL: `https://bidding-eventcollector-stage.azurewebsites.net`
  - Performance URL: `https://bidding-prfmnccollector-stage.azurewebsites.net`

#### 4. SecurePreferences

Hassas verilerin güvenli saklanması için kullanılan utility sınıfıdır.

**Özellikler:**
- AES-256-GCM şifreleme
- EncryptedSharedPreferences kullanımı
- Thread-safe singleton pattern
- Fallback mekanizması (şifreleme başarısız olursa normal SharedPreferences)

**Saklanan Veriler:**
- Session ID (`session_id`)
- Session Timestamp (`session_timestamp`)
- Anonymous ID (`anonymous_id`)

**Güvenlik:**
- Master Key: AES256_GCM scheme
- Key Encryption: AES256_SIV
- Value Encryption: AES256_GCM

#### 5. InputValidator

Kullanıcı girdilerini doğrulayan ve temizleyen utility sınıfıdır.

**Validation Özellikleri:**
- Uzunluk kontrolü (her parametre için maksimum uzunluk)
- SQL injection koruması
- XSS koruması (HTML tag temizleme)
- Script tag temizleme
- Null byte temizleme

**Maksimum Uzunluklar:**
- `userId`: 256 karakter
- `keyword`: 256 karakter
- `lineItemIds`, `categoryId`, vb.: 1024 karakter
- `productList`: 10240 karakter
- `payload`: 65536 karakter

**Sanitization İşlemleri:**
- HTML tag'lerinin temizlenmesi
- Script tag'lerinin kaldırılması
- SQL injection pattern'lerinin tespiti
- Uzunluk limitlerinin uygulanması
- Trim işlemi

#### 6. DeviceInfo

Cihaz bilgilerini toplayan utility sınıfıdır.

**Toplanan Bilgiler:**
- **Device ID:** Android ID (veya UUID fallback)
- **App Name:** Package name (AndroidManifest'ten)
- **OS:** "Android" (sabit)
- **Language:** Locale bilgisi (örn: "tr-TR", "en-US")

**Device ID Yönetimi:**
- Öncelik: Android ID (`Settings.Secure.ANDROID_ID`)
- Fallback: UUID (Android ID alınamazsa)
- Thread-safe initialization

---

### Event Tracking Sistemi

#### Event Türleri

**1. Standard Events:**
- `HOME` - Ana sayfa etkileşimleri
- `LISTING` - Ürün listesi etkileşimleri
- `SEARCH` - Arama etkileşimleri
- `PDP` - Ürün detay sayfası etkileşimleri
- `CART` - Sepet etkileşimleri
- `PURCHASE` - Satın alma işlemleri

**2. Event Parametreleri:**
- `VIEW` - Görüntüleme
- `ADD_TO_CART` - Sepete ekleme
- `ADD_TO_FAVORITES` - Favorilere ekleme
- `SUCCESS` - Başarılı işlem

**3. Performance Events:**
- `IMPRESSION` - Reklam görüntülenme
- `CLICK` - Reklam tıklama

#### Event Parametreleri

**Sistem Tarafından Otomatik Üretilen Parametreler:**
- `v` (sdkVersion): SDK versiyonu
- `app` (appName): Uygulama paket adı
- `t` (timestamp): Unix timestamp (milliseconds)
- `d` (deviceId): Cihaz ID
- `os` (os): İşletim sistemi ("Android")
- `lng` (language): Cihaz dili ve ülke kodu
- `en` (eventName): Event adı
- `ep` (eventParameter): Event parametresi
- `tid` (traceId): Her event için benzersiz UUID
- `s` (sessionId): Oturum ID (30 dakika geçerlilik)
- `aid` (anonymousId): Anonim kullanıcı ID

**Kullanıcı Tarafından Sağlanan Parametreler:**
- `uid` (userId): Kullanıcı ID (opsiyonel)
- `li` (lineItemIds): Ürün ID'leri (virgülle ayrılmış)
- `pl` (productList): Ürün listesi
- `ct` (categoryId): Kategori ID
- `kw` (keyword): Arama kelimesi
- `lc` (loyaltyCard): Loyalty card numarası
- `trans` (transactionId): İşlem ID
- `trc` (totalRowCount): Toplam satır sayısı

---

### Thread Safety ve Performans

#### Thread Safety Özellikleri

**1. MimedaSDK:**
- Singleton pattern (`object` keyword)
- `@Volatile` annotation'ları ile görünürlük garantisi
- `@Synchronized` initialize metodu ile race condition koruması

**2. EventTracker:**
- Single-threaded executor (`Executors.newSingleThreadExecutor()`)
- Event sıralamasının korunması
- Thread-safe session ve anonymous ID yönetimi

**3. SecurePreferences:**
- Thread-safe singleton pattern
- Synchronized initialization
- Thread-safe read/write işlemleri

#### Performans Optimizasyonları

**1. Asenkron İşleme:**
- Tüm event gönderimleri arka plan thread'inde çalışır
- UI thread'i asla bloklanmaz
- Single-threaded executor ile sıralama korunur

**2. Automatic Retry:**
- Exponential backoff ile akıllı retry
- Network hatalarında otomatik yeniden deneme
- Timeout yönetimi

**3. ProGuard/R8 Optimizasyonu:**
- Release build'lerde kod obfuscation
- Consumer ProGuard rules ile otomatik optimizasyon
- Minimal APK boyutu

**4. Güvenli Depolama:**
- EncryptedSharedPreferences ile verimli şifreleme
- Lazy initialization
- Fallback mekanizması

---

### Güvenlik Özellikleri

#### 1. Veri Şifreleme

**EncryptedSharedPreferences:**
- AES-256-GCM şifreleme
- Master Key: AES256_GCM scheme
- Key Encryption: AES256_SIV
- Value Encryption: AES256_GCM

**Şifrelenen Veriler:**
- Session ID
- Session Timestamp
- Anonymous ID

#### 2. Input Validation ve Sanitization

**Koruma Türleri:**
- SQL Injection koruması
- XSS koruması (HTML tag temizleme)
- Script tag temizleme
- Null byte temizleme
- Uzunluk validasyonu

**Validation Kuralları:**
- Tüm string parametreler uzunluk kontrolünden geçer
- Tehlikeli pattern'ler tespit edilir ve temizlenir
- HTML ve script tag'leri kaldırılır

#### 3. ProGuard Obfuscation

**Release Build'lerde:**
- Kod obfuscation aktif
- Consumer ProGuard rules otomatik uygulanır
- API sınıfları korunur, internal sınıflar obfuscate edilir

---

### CI/CD Pipeline

#### GitHub Actions Workflow

**Workflow Dosyası:** `.github/workflows/pr-check.yml`

**Job'lar:**
1. **build-and-test:** Proje build edilir, unit testler çalıştırılır, coverage raporu oluşturulur
2. **lint:** Android Lint çalıştırılır, kod kalitesi kontrol edilir
3. **deploy:** Artifact'lar oluşturulur, imzalanır ve Maven Central'a yüklenir

#### Branch Stratejisi

**Staging Branch:**
- Branch: `staging`
- Versiyon Formatı: `1.0.0-beta.{run_number}-SNAPSHOT`
- Deployment: Otomatik (Snapshot repository)
- Repository: `https://central.sonatype.com/repository/maven-snapshots/`

**Production Branch:**
- Branch: `master` veya `main`
- Versiyon: `gradle.properties` dosyasındaki `MIMEDA_SDK_VERSION`
- Deployment: Manual approval gerekir
- Repository: Maven Central

#### Deployment Süreci

**Staging Deployment:**
1. `staging` branch'e push yapılır
2. CI/CD pipeline otomatik çalışır
3. Versiyon otomatik oluşturulur: `1.0.0-beta.{run_number}-SNAPSHOT`
4. Snapshot repository'ye otomatik deploy edilir

**Production Deployment:**
1. `gradle.properties` dosyasındaki `MIMEDA_SDK_VERSION` değeri artırılır
2. Değişiklikler `master`/`main` branch'e merge edilir
3. CI/CD pipeline otomatik çalışır
4. Production repository'ye bundle yüklenir
5. Sonatype Central Portal'da manual approval gerekir
6. Approval sonrası versiyon Maven Central'da yayınlanır

#### Artifact İmzalama

**GPG İmzalama:**
- Tüm artifact'lar (AAR, POM, JAR) GPG ile imzalanır
- `.asc` dosyaları oluşturulur
- Checksum'lar (MD5, SHA1) oluşturulur

**Bundle Oluşturma:**
- Maven Central formatında bundle oluşturulur
- Tüm gerekli dosyalar (AAR, POM, JAR, ASC, checksum'lar) dahil edilir
- ZIP formatında paketlenir

---

### Bağımlılıklar ve Versiyonlar

#### SDK Bağımlılıkları

**Transitive Dependencies (API):**
- `com.squareup.okhttp3:okhttp:4.12.0` - HTTP client
- `com.squareup.okhttp3:logging-interceptor:4.12.0` - HTTP logging (debug)
- `com.google.code.gson:gson:2.10.1` - JSON serialization

**Runtime Dependencies:**
- `androidx.core:core-ktx:1.17.0` - Kotlin extensions
- `androidx.security:security-crypto:1.1.0-alpha06` - EncryptedSharedPreferences

#### Minimum Gereksinimler

- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** Latest stable Android SDK
- **Kotlin:** 1.9.0+
- **Java:** 11+
- **Gradle:** 8.0+

#### Build Konfigürasyonu

**Gradle Properties:**
- `MIMEDA_SDK_VERSION`: SDK versiyonu (örn: "1.0.0")
- `MIMEDA_CONNECT_TIMEOUT`: Connection timeout (saniye, varsayılan: 10)
- `MIMEDA_READ_TIMEOUT`: Read timeout (saniye, varsayılan: 30)
- `MIMEDA_WRITE_TIMEOUT`: Write timeout (saniye, varsayılan: 30)
- `MIMEDA_MAX_RETRIES`: Maksimum retry sayısı (varsayılan: 3)
- `MIMEDA_RETRY_BASE_DELAY_MS`: Retry base delay (milisaniye, varsayılan: 1000)

---

### Hata Yönetimi ve Callback Sistemi

#### MimedaSDKErrorCallback

SDK, hata durumlarını yakalamak için callback interface'i sağlar:

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
    
    fun onValidationFailed(
        eventName: EventName?,
        errors: List<String>
    )
}
```

**Callback Kullanım Senaryoları:**
- Event tracking başarısız olduğunda
- Performance event tracking başarısız olduğunda
- Input validation başarısız olduğunda

**Hata Türleri:**
- Network hataları (timeout, connection error)
- Validation hataları (uzunluk, format)
- API hataları (4xx, 5xx)
- Unexpected hatalar

---

### Debug ve Logging

#### Debug Logging

**Aktifleştirme:**
```kotlin
MimedaSDK.setDebugLogging(true)
```

**Log Türleri:**
- Success logs: Event başarıyla gönderildiğinde
- Error logs: Hata durumlarında
- Info logs: Bilgilendirme mesajları

**Log Tag:**
- Tüm loglar `MimedaSDK` tag'i ile Logcat'te görüntülenir

**Önemli Notlar:**
- Debug logging, release build'lerde de çalışır (eğer açılırsa)
- Production'da debug logging'i açık bırakılmamalıdır
- Loglar sensitive bilgi içermez (güvenlik için)

---

### Test Stratejisi

#### Unit Testler

**Test Kapsamı:**
- API Service testleri (MockWebServer ile)
- Input validation testleri
- SecurePreferences testleri
- DeviceInfo testleri
- Event tracking testleri

**Test Coverage:**
- Minimum %60 coverage gereksinimi
- PR'larda otomatik coverage raporu
- Coverage raporu PR'a yorum olarak eklenir

#### Instrumented Testler

**Android Testler:**
- SDK initialization testleri
- Event tracking integration testleri
- SecurePreferences encryption testleri
- Session management testleri

---

### ProGuard Kuralları

#### Consumer ProGuard Rules

SDK, `consumer-rules.pro` dosyası ile otomatik ProGuard kuralları sağlar.

**Korunan Sınıflar:**
- Public API sınıfları
- Reflection kullanılan sınıflar
- Serialization sınıfları

**Obfuscation:**
- Internal sınıflar obfuscate edilir
- Public API korunur
- Release build'lerde otomatik uygulanır

---

### Versiyonlama Stratejisi

#### Semantic Versioning

SDK, Semantic Versioning (SemVer) kullanır:
- **Major:** Breaking changes (örn: 1.0.0 → 2.0.0)
- **Minor:** Yeni özellikler, geriye uyumlu (örn: 1.0.0 → 1.1.0)
- **Patch:** Bug fix'ler (örn: 1.0.0 → 1.0.1)

#### Versiyon Formatları

**Production:**
- Format: `MAJOR.MINOR.PATCH` (örn: `1.0.0`)
- `gradle.properties` dosyasında manuel olarak belirlenir

**Staging:**
- Format: `MAJOR.MINOR.PATCH-beta.RUN_NUMBER-SNAPSHOT` (örn: `1.0.0-beta.36-SNAPSHOT`)
- Otomatik olarak CI/CD tarafından oluşturulur

---

### Sorun Giderme

#### Yaygın Sorunlar ve Çözümleri

**1. SDK Başlatılmadı Hatası:**
```kotlin
if (!MimedaSDK.isInitialized()) {
    MimedaSDK.initialize(context, apiKey)
}
```

**2. Event Tracking Çalışmıyor:**
- SDK'nın başlatıldığından emin olun
- API key'in doğru olduğunu kontrol edin
- Internet izninin verildiğinden emin olun
- Debug logging'i açıp logları kontrol edin

**3. Loglar Görünmüyor:**
```kotlin
MimedaSDK.setDebugLogging(true)
// Logcat'te "MimedaSDK" tag'ini filtreleyin
```

**4. Deployment Başarısız:**
- GitHub Secrets'ların doğru eklendiğini kontrol edin
- GPG key'in doğru formatlandığını kontrol edin
- Token'ların geçerli olduğunu kontrol edin

---

### Sonuç

Bu dokümantasyon, Mimeda Bidding Android SDK'nın sıfırdan nasıl oluşturulduğunu, yayınlandığını ve yönetildiğini kapsamlı bir şekilde açıklamaktadır. SDK'nın tüm teknik detayları, güvenlik özellikleri, CI/CD pipeline'ı ve yönetimsel işlemler bu dokümantasyonda yer almaktadır.
