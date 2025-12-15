# Mimeda SDK Consumer ProGuard Rules
# These rules are automatically included when the SDK is used

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

