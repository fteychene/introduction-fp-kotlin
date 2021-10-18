class App(val storage: Storage<Cart>) {

    fun applyDiscount(cartId: CartId) {
        val cart = loadCart(cartId)
        if (cart != null) {
            discount(cart)
        }
    }

    private fun loadCart(id: CartId): Cart? {
        if (id.contains("gold"))
            return Cart(id, "gold-customer", 100.0)
        if (id.contains("normal"))
            return Cart(id, "standard-customer", 100.0)
        return null
    }

    private fun discount(cart: Cart, ) {
        if(cart.customer.contains("gold")) {
            cart.amount /= 2
            storage.flush(cart)
        }
    }
}