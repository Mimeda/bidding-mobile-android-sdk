# Changelog

Tüm önemli değişiklikler bu dosyada belgelenecektir.

Format [Keep a Changelog](https://keepachangelog.com/tr/1.0.0/) standardına uygundur,
ve bu proje [Semantic Versioning](https://semver.org/lang/tr/) kullanır.

## [1.0.0] - 2024-XX-XX

### Eklenenler
- İlk SDK sürümü
- Event tracking özelliği
- API key + package name doğrulaması
- Timestamp kontrolü (replay attack önleme)
- Background thread'de event gönderimi
- Kapsamlı error handling (SDK hiçbir zaman crash etmez)
- BuildConfig ile yapılandırma yönetimi
- ProGuard desteği
- Memory leak önleme (shutdown metodu)

### Güvenlik
- Package name otomatik alınıyor (geliştirici tarafından verilemez)
- API key + package name kombinasyonu backend'de doğrulanıyor
- Timestamp kontrolü ile replay attack önleme

### Dokümantasyon
- README.md - Kullanım kılavuzu
- TECH.md - Teknik dokümantasyon
- CHANGELOG.md - Versiyon geçmişi

[1.0.0]: https://github.com/mimeda/mimeda-android-sdk/releases/tag/v1.0.0

