package com.mimeda.sdk.api

import com.mimeda.sdk.utils.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * HTTP Client yapılandırması
 * OkHttp kullanarak API çağrıları için client oluşturur
 */
internal object ApiClient {
    private val CONNECT_TIMEOUT = com.mimeda.sdk.BuildConfig.CONNECT_TIMEOUT_SECONDS
    private val READ_TIMEOUT = com.mimeda.sdk.BuildConfig.READ_TIMEOUT_SECONDS
    private val WRITE_TIMEOUT = com.mimeda.sdk.BuildConfig.WRITE_TIMEOUT_SECONDS

    /**
     * OkHttpClient instance'ı oluşturur
     * Timeout değerleri ve logging interceptor ile yapılandırılmış
     * @param apiKey API anahtarı
     * @param packageName Paket adı (backend'de API key ile birlikte doğrulanır)
     */
    fun createClient(apiKey: String, packageName: String): OkHttpClient {
        return try {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (Logger.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

            OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .header("X-API-Key", apiKey)
                        .header("X-Package-Name", packageName)
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .addInterceptor(loggingInterceptor)
                .build()
        } catch (e: Exception) {
            Logger.e("Failed to create OkHttpClient", e)
            // Fallback: Basit bir client döndür
            OkHttpClient()
        }
    }
}

