# Changelogs

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
- Retry mechanism
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
- ProGuard obfuscation

