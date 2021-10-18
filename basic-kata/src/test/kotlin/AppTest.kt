import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class AppTest {

    @Test
    fun happyPath() {
        val cartId = "some-gold-cart"
        val storage = SpyStorage()

        val app = App(storage)
        app.applyDiscount(cartId)

        val expected = Cart("some-gold-cart", "gold-customer", 50.0)
        assertEquals(storage.saved, expected)
    }

    @Test
    fun noDiscount() {
        val cartId = "some-normal-cart"
        val storage = SpyStorage()

        val app = App(storage)
        app.applyDiscount(cartId)

        assertEquals(storage.saved, null)
    }

    @Test
    fun missingCart() {
        val cartId = "missing-cart"
        val storage = SpyStorage()

        val app = App(storage)
        app.applyDiscount(cartId)

        assertEquals(storage.saved, null)
    }

    internal inner class SpyStorage : Storage<Cart> {
        var saved: Cart? = null

        override fun flush(item: Cart) {
            saved = item
        }
    }
}