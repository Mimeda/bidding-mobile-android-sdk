# Keep line number information for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================
# Mimeda SDK ProGuard Rules
# ============================================

# ============================================
# PUBLIC API
# ============================================

-keep public class com.mimeda.sdk.MimedaSDK {
    public *;
}

-keep public enum com.mimeda.sdk.Environment {
    *;
}

-keep public interface com.mimeda.sdk.MimedaSDKErrorCallback {
    *;
}

-keep public enum com.mimeda.sdk.events.EventName {
    *;
}

-keep public enum com.mimeda.sdk.events.EventParameter {
    *;
}

-keep public enum com.mimeda.sdk.events.PerformanceEventType {
    *;
}

-keep public class com.mimeda.sdk.events.EventParams {
    *;
}

-keep public class com.mimeda.sdk.events.PerformanceEventParams {
    *;
}

# ============================================
# INTERNAL CLASSES
# ============================================

-keepclassmembers class com.mimeda.sdk.api.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers class com.mimeda.sdk.events.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================
# OkHttp Rules
# ============================================

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ============================================
# Gson Rules
# ============================================

-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# ============================================
# Android Rules
# ============================================

-keep class android.content.Context { *; }
