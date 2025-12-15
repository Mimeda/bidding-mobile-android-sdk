package com.mimeda.sdk.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Güvenli veri depolama için EncryptedSharedPreferences wrapper.
 * Android Keystore ile AES-256 şifreleme kullanır.
 */
internal object SecurePreferences {
    private const val PREFS_NAME = "mimeda_sdk_secure_prefs"
    
    @Volatile
    private var encryptedPrefs: SharedPreferences? = null
    
    /**
     * EncryptedSharedPreferences instance'ını döndürür.
     * Thread-safe singleton pattern kullanır.
     */
    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        return encryptedPrefs ?: synchronized(this) {
            encryptedPrefs ?: createEncryptedPrefs(context).also {
                encryptedPrefs = it
            }
        }
    }
    
    /**
     * EncryptedSharedPreferences oluşturur.
     * MasterKey ile AES256-GCM şifreleme kullanır.
     */
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
            // Fallback: Normal SharedPreferences (güvenlik riski var ama crash'den iyidir)
            context.getSharedPreferences(PREFS_NAME + "_fallback", Context.MODE_PRIVATE)
        }
    }
    
    /**
     * Şifrelenmiş string değer okur.
     */
    fun getString(context: Context, key: String, defaultValue: String? = null): String? {
        return try {
            getEncryptedPrefs(context).getString(key, defaultValue)
        } catch (e: Exception) {
            Logger.e("Failed to read encrypted value for key: $key", e)
            defaultValue
        }
    }
    
    /**
     * Şifrelenmiş string değer yazar.
     */
    fun putString(context: Context, key: String, value: String) {
        try {
            getEncryptedPrefs(context).edit().putString(key, value).apply()
        } catch (e: Exception) {
            Logger.e("Failed to write encrypted value for key: $key", e)
        }
    }
    
    /**
     * Şifrelenmiş boolean değer okur.
     */
    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return try {
            getEncryptedPrefs(context).getBoolean(key, defaultValue)
        } catch (e: Exception) {
            Logger.e("Failed to read encrypted boolean for key: $key", e)
            defaultValue
        }
    }
    
    /**
     * Şifrelenmiş boolean değer yazar.
     */
    fun putBoolean(context: Context, key: String, value: Boolean) {
        try {
            getEncryptedPrefs(context).edit().putBoolean(key, value).apply()
        } catch (e: Exception) {
            Logger.e("Failed to write encrypted boolean for key: $key", e)
        }
    }
    
    /**
     * Şifrelenmiş long değer okur.
     */
    fun getLong(context: Context, key: String, defaultValue: Long = 0L): Long {
        return try {
            getEncryptedPrefs(context).getLong(key, defaultValue)
        } catch (e: Exception) {
            Logger.e("Failed to read encrypted long for key: $key", e)
            defaultValue
        }
    }
    
    /**
     * Şifrelenmiş long değer yazar.
     */
    fun putLong(context: Context, key: String, value: Long) {
        try {
            getEncryptedPrefs(context).edit().putLong(key, value).apply()
        } catch (e: Exception) {
            Logger.e("Failed to write encrypted long for key: $key", e)
        }
    }
    
    /**
     * Belirtilen key'i siler.
     */
    fun remove(context: Context, key: String) {
        try {
            getEncryptedPrefs(context).edit().remove(key).apply()
        } catch (e: Exception) {
            Logger.e("Failed to remove encrypted value for key: $key", e)
        }
    }
    
    /**
     * Tüm şifrelenmiş verileri temizler.
     */
    fun clear(context: Context) {
        try {
            getEncryptedPrefs(context).edit().clear().apply()
        } catch (e: Exception) {
            Logger.e("Failed to clear encrypted preferences", e)
        }
    }
    
    /**
     * Belirtilen key'in var olup olmadığını kontrol eder.
     */
    fun contains(context: Context, key: String): Boolean {
        return try {
            getEncryptedPrefs(context).contains(key)
        } catch (e: Exception) {
            Logger.e("Failed to check if key exists: $key", e)
            false
        }
    }
}
