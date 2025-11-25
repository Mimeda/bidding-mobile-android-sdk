package com.mimeda.sdk.events

/**
 * Event adları - Query parametresinde "en" olarak kullanılır
 */
enum class EventName(val value: String) {
    /**
     * Ana sayfa eventi
     */
    HOME("home"),
    
    /**
     * Ürün listeleme sayfası eventi (search hariç)
     */
    LISTING("listing"),
    
    /**
     * Arama sonuçları sayfası eventi
     */
    SEARCH("search"),
    
    /**
     * Ürün detay sayfası eventi (Product Detail Page)
     */
    PDP("pdp"),
    
    /**
     * Sepet sayfası eventi
     */
    CART("cart"),
    
    /**
     * Satın alma işlemi eventi
     */
    PURCHASE("purchase")
}

