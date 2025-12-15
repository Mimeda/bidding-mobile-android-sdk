package com.mimeda.sdk

import android.content.Context
import com.mimeda.sdk.utils.SecurePreferences
import okhttp3.mockwebserver.RecordedRequest

object TestHelpers {
    fun getSessionIdFromPrefs(context: Context): String? {
        return SecurePreferences.getString(context, "session_id")
    }
    
    fun getAnonymousIdFromPrefs(context: Context): String? {
        return SecurePreferences.getString(context, "anonymous_id")
    }
    
    fun getTraceIdFromRequest(request: RecordedRequest): String? {
        return request.requestUrl?.queryParameter("tid")
    }
    
    fun getTimestampFromRequest(request: RecordedRequest): Long? {
        return request.requestUrl?.queryParameter("t")?.toLongOrNull()
    }
    
    fun clearSharedPreferences(context: Context) {
        SecurePreferences.clear(context)
    }
}
