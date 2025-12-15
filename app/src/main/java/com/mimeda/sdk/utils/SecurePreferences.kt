package com.mimeda.sdk.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

internal object SecurePreferences {
    private const val PREFS_NAME = "mimeda_sdk_secure_prefs"
    
    @Volatile
    private var encryptedPrefs: SharedPreferences? = null
    
    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        return encryptedPrefs ?: synchronized(this) {
            encryptedPrefs ?: createEncryptedPrefs(context).also {
                encryptedPrefs = it
            }
        }
    }
    
    private fun createEncryptedPrefs(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Logger.e("Failed to create EncryptedSharedPreferences, falling back to regular prefs", e)
            context.getSharedPreferences(PREFS_NAME + "_fallback", Context.MODE_PRIVATE)
        }
    }
    
    fun getString(context: Context, key: String, defaultValue: String? = null): String? {
        return try {
            getEncryptedPrefs(context).getString(key, defaultValue)
        } catch (e: Exception) {
            Logger.e("Failed to read encrypted value for key: $key", e)
            defaultValue
        }
    }
    
    fun putString(context: Context, key: String, value: String) {
        try {
            getEncryptedPrefs(context).edit().putString(key, value).apply()
        } catch (e: Exception) {
            Logger.e("Failed to write encrypted value for key: $key", e)
        }
    }
    
    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return try {
            getEncryptedPrefs(context).getBoolean(key, defaultValue)
        } catch (e: Exception) {
            Logger.e("Failed to read encrypted boolean for key: $key", e)
            defaultValue
        }
    }
    
    fun putBoolean(context: Context, key: String, value: Boolean) {
        try {
            getEncryptedPrefs(context).edit().putBoolean(key, value).apply()
        } catch (e: Exception) {
            Logger.e("Failed to write encrypted boolean for key: $key", e)
        }
    }
    
    fun getLong(context: Context, key: String, defaultValue: Long = 0L): Long {
        return try {
            getEncryptedPrefs(context).getLong(key, defaultValue)
        } catch (e: Exception) {
            Logger.e("Failed to read encrypted long for key: $key", e)
            defaultValue
        }
    }
    
    fun putLong(context: Context, key: String, value: Long) {
        try {
            getEncryptedPrefs(context).edit().putLong(key, value).apply()
        } catch (e: Exception) {
            Logger.e("Failed to write encrypted long for key: $key", e)
        }
    }
    
    fun remove(context: Context, key: String) {
        try {
            getEncryptedPrefs(context).edit().remove(key).apply()
        } catch (e: Exception) {
            Logger.e("Failed to remove encrypted value for key: $key", e)
        }
    }
    
    fun clear(context: Context) {
        try {
            getEncryptedPrefs(context).edit().clear().apply()
        } catch (e: Exception) {
            Logger.e("Failed to clear encrypted preferences", e)
        }
    }
    
    fun contains(context: Context, key: String): Boolean {
        return try {
            getEncryptedPrefs(context).contains(key)
        } catch (e: Exception) {
            Logger.e("Failed to check if key exists: $key", e)
            false
        }
    }
}
