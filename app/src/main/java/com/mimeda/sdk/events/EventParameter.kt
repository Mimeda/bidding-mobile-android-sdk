package com.mimeda.sdk.events

/**
 * Event parametreleri - Query parametresinde "ep" olarak kullanılır
 */
enum class EventParameter(val value: String) {
    /**
     * Sayfa görüntüleme
     */
    VIEW("view"),
    
    /**
     * Sepete ekleme
     */
    ADD_TO_CART("addtocart"),
    
    /**
     * Favorilere ekleme
     */
    ADD_TO_FAVORITES("addtofavorites"),
    
    /**
     * Başarılı işlem (sadece Purchase için)
     */
    SUCCESS("success")
}

