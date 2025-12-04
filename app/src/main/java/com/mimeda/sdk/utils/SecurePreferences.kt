package com.mimeda.sdk.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.nio.charset.StandardCharsets

internal object SecurePreferences {
    private fun obfuscateKey(key: String): String {
        return Base64.encodeToString(key.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    }
    
    private fun obfuscateValue(value: String): String {
        return Base64.encodeToString(value.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    }
    
    private fun deobfuscateValue(value: String): String? {
        return try {
            val decoded = Base64.decode(value, Base64.NO_WRAP)
            String(decoded, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }
    
    fun getString(prefs: SharedPreferences, key: String, defaultValue: String?): String? {
        val obfuscatedKey = obfuscateKey(key)
        val obfuscatedValue = prefs.getString(obfuscatedKey, null) ?: return defaultValue
        return deobfuscateValue(obfuscatedValue) ?: defaultValue
    }
    
    fun putString(editor: SharedPreferences.Editor, key: String, value: String): SharedPreferences.Editor {
        val obfuscatedKey = obfuscateKey(key)
        val obfuscatedValue = obfuscateValue(value)
        return editor.putString(obfuscatedKey, obfuscatedValue)
    }
}

