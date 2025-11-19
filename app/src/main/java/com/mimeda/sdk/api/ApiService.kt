package com.mimeda.sdk.api

import com.google.gson.Gson
import com.mimeda.sdk.utils.Logger
import com.mimeda.sdk.utils.TimestampGenerator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * API Service - HTTP isteklerini yönetir
 * Event tracking için API endpoint'lerine istek gönderir
 */
internal class ApiService(private val client: okhttp3.OkHttpClient) {
    private val gson = Gson()
    private val baseUrl = com.mimeda.sdk.BuildConfig.API_BASE_URL
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Event tracking için API'ye POST isteği gönderir
     * @param eventName Event adı
     * @param params Event parametreleri
     * @return Başarılı olursa true, aksi halde false (exception fırlatmaz)
     */
    fun trackEvent(eventName: String, params: Map<String, Any>): Boolean {
        return try {
            val eventData = mapOf(
                "event_name" to eventName,
                "params" to params,
                "timestamp" to System.currentTimeMillis()
            )

            val jsonBody = gson.toJson(eventData)
            val timestamp = TimestampGenerator.getTimestamp()
            val requestBody = jsonBody.toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("$baseUrl/events")
                .post(requestBody)
                .header("X-Timestamp", timestamp.toString())
                .build()

            val response = client.newCall(request).execute()

            val isSuccess = response.isSuccessful
            if (isSuccess) {
                Logger.d("Event tracked successfully: $eventName")
            } else {
                Logger.e("Event tracking failed: ${response.code} - ${response.message}")
            }

            response.close()
            isSuccess
        } catch (e: IOException) {
            Logger.e("Network error while tracking event: $eventName", e)
            false
        } catch (e: Exception) {
            Logger.e("Unexpected error while tracking event: $eventName", e)
            false
        }
    }
}

