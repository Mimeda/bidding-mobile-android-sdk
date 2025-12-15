# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line number information for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================
# Mimeda SDK ProGuard Rules
# ============================================

# ============================================
# PUBLIC API - Bu sınıflar korunmalı (obfuscate edilmez)
# ============================================

# Main SDK entry point
-keep public class com.mimeda.sdk.MimedaSDK {
    public *;
}

# Environment enum
-keep public enum com.mimeda.sdk.Environment {
    *;
}

# Error callback interface
-keep public interface com.mimeda.sdk.MimedaSDKErrorCallback {
    *;
}

# Public event classes
-keep public enum com.mimeda.sdk.events.EventName {
    *;
}

-keep public enum com.mimeda.sdk.events.EventParameter {
    *;
}

-keep public enum com.mimeda.sdk.events.PerformanceEventType {
    *;
}

# Public data classes - tüm field'ları koru
-keep public class com.mimeda.sdk.events.EventParams {
    *;
}

-keep public class com.mimeda.sdk.events.PerformanceEventParams {
    *;
}

# ============================================
# INTERNAL CLASSES - Bu sınıflar obfuscate edilir
# Sadece Gson serialization için gerekli anotasyonlar korunur
# ============================================

# Keep Gson serialized fields in internal classes
-keepclassmembers class com.mimeda.sdk.api.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers class com.mimeda.sdk.events.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================
# OkHttp Rules
# ============================================

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Keep OkHttp classes
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ============================================
# Gson Rules
# ============================================

# Keep Gson classes
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures for Gson
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep generic signature of Map, List, Set, etc.
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# ============================================
# Android Rules
# ============================================

# Keep Android Context
-keep class android.content.Context { *; }