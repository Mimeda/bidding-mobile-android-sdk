# Changelogs

## [1.0.1] - Unreleased

### Changed
- Validasyon mantığı backend tarafına taşınacak.
- SDK artık gelen verileri doğrudan POST ediyor, zorunlu alan kontrolü backend'de yapılacak.
- `PerformanceEventParams` alanları nullable yapıldı (tümü opsiyonel)
- Sanitization (XSS, HTML tag temizliği, SQL injection koruması) SDK tarafında korundu

### Removed
- `MimedaSDKErrorCallback.onValidationFailed()` callback'i kaldırıldı
- `InputValidator.validateEventParams()` ve `validatePerformanceEventParams()` fonksiyonları kaldırıldı
- `ApiService` içindeki validasyon mantığı kaldırıldı

### Security
- SQL injection pattern koruması güncellendi
- Sanitization fonksiyonları korundu (XSS, HTML tag, null byte temizliği)

---

## [1.0.0](https://central.sonatype.com/artifact/tr.com.mimeda/bidding-mobile-android-sdk/1.0.0) - 16.12.2025 

### Added
- İlk stabil sürüm
- Event tracking desteği
- Performance monitoring desteği
- EncryptedSharedPreferences entegrasyonu
  - Session ID ve Anonymous ID güvenli saklama
  - AES-256-GCM şifreleme
- Input validation ve sanitization
  - SQL injection koruması
  - XSS koruması
  - HTML tag temizleme
  - Uzunluk validasyonu
- ProGuard/R8 optimizasyonu
  - Consumer ProGuard rules
  - Kod obfuscation
- Debug logging desteği
  - Runtime debug logging kontrolü
  - Logcat entegrasyonu
- Automatic Retry
  - Ağ hatalarında otomatik yeniden deneme
  - Exponential backoff
- Environment support
  - Production ve Staging ortamları
  - Environment bazlı URL yapılandırması
- Maven Central deployment
  - Snapshot repository desteği
  - Release deployment
  - GPG signing

### Security
- EncryptedSharedPreferences ile hassas veri saklama
- Input sanitization ve validation
- ProGuard / R8 Obfuscation

