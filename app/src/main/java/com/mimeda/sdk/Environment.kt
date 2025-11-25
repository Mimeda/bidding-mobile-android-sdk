package com.mimeda.sdk

/**
 * Environment enum - SDK'nın hangi environment'a bağlanacağını belirler
 * 
 * Kullanım:
 * ```
 * MimedaSDK.initialize(context, apiKey, Environment.PRODUCTION)
 * MimedaSDK.initialize(context, apiKey, Environment.STAGING)
 * ```
 */
enum class Environment {
    /**
     * Production environment
     * Production API endpoint'lerine bağlanır
     */
    PRODUCTION,
    
    /**
     * Staging environment
     * Staging/test API endpoint'lerine bağlanır
     */
    STAGING
}

