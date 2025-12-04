package com.mimeda.sdk

import com.mimeda.sdk.events.EventName
import com.mimeda.sdk.events.EventParameter
import com.mimeda.sdk.events.PerformanceEventType

interface MimedaSDKErrorCallback {
    fun onEventTrackingFailed(
        eventName: EventName,
        eventParameter: EventParameter,
        error: Throwable
    )
    
    fun onPerformanceEventTrackingFailed(
        eventType: PerformanceEventType,
        error: Throwable
    )
    
    fun onValidationFailed(
        eventName: EventName?,
        errors: List<String>
    )
}

