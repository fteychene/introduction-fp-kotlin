package solution

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AppTest {
    @Test
    fun happyPath() {
        val expected = AppliedDiscount(cart = Cart.ExistingCart("some-gold-cart", CustomerId.GoldCustomer, 50.0))
        assertEquals(App.applyDiscount( "some-gold-cart"), expected)
    }

    @Test
    fun noDiscount() {
        assertEquals(App.applyDiscount( "some-normal-cart"), NoDiscount)
    }

    @Test
    fun missingCart() {
        assertEquals(App.applyDiscount( "missing-cart"), NoDiscount)
    }
}