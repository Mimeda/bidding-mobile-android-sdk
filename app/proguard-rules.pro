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

# Keep SDK public API and all internal classes
-keep class com.mimeda.sdk.** { *; }
-keepclassmembers class com.mimeda.sdk.** { *; }

# Keep enums
-keepclassmembers enum com.mimeda.sdk.** {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data classes
-keepclassmembers class com.mimeda.sdk.events.** { *; }
-keepclassmembers class com.mimeda.sdk.api.** { *; }
-keepclassmembers class com.mimeda.sdk.utils.** { *; }

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