package com.mimeda.sdk

import android.content.Context
import com.mimeda.sdk.utils.SecurePreferences
import okhttp3.mockwebserver.RecordedRequest

object TestHelpers {
    fun getSessionIdFromPrefs(context: Context): String? {
        val prefs = context.getSharedPreferences("mimeda_sdk_session", Context.MODE_PRIVATE)
        return SecurePreferences.getString(prefs, "session_id", null)
    }
    
    fun getAnonymousIdFromPrefs(context: Context): String? {
        val prefs = context.getSharedPreferences("mimeda_sdk_session", Context.MODE_PRIVATE)
        return SecurePreferences.getString(prefs, "anonymous_id", null)
    }
    
    fun getTraceIdFromRequest(request: RecordedRequest): String? {
        return request.requestUrl?.queryParameter("tid")
    }
    
    fun getTimestampFromRequest(request: RecordedRequest): Long? {
        return request.requestUrl?.queryParameter("t")?.toLongOrNull()
    }
    
    fun clearSharedPreferences(context: Context) {
        context.getSharedPreferences("mimeda_sdk_session", Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}

