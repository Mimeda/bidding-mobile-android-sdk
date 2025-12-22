package com.mimeda.sdk.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InputValidatorTest {

    @Test
    fun sanitizeProductList_keepsDelimitersAndDoesNotTruncate() {
        val input = "SKU1:1:10.0;SKU2:2:20.0"
        val sanitized = InputValidator.sanitizeProductList(input)
        assertEquals(input, sanitized)

        val longSku = "SKU:1:10.0"
        val longInput = (0 until 2000).joinToString(";") { longSku } // > 10240 chars
        val longSanitized = InputValidator.sanitizeProductList(longInput)
        assertEquals(longInput.length, longSanitized?.length)
        assertEquals(longInput, longSanitized)
    }

    @Test
    fun sanitizeProductList_removesHtmlAndScriptAndNullChar() {
        val input = "  SKU1:1:10.0;\u0000<script>alert(1)</script><b>SKU2</b>:2:20.0  "
        val sanitized = InputValidator.sanitizeProductList(input)
        assertEquals("SKU1:1:10.0;SKU2:2:20.0", sanitized)
    }

    @Test
    fun sanitizeProductList_nullOrBlank_passthrough() {
        assertNull(InputValidator.sanitizeProductList(null))
        assertEquals("", InputValidator.sanitizeProductList(""))
        assertEquals("   ", InputValidator.sanitizeProductList("   "))
    }
}

