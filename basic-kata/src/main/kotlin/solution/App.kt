package solution

object App {

    fun applyDiscount(cartId: CartId): Discount =
        when (val cart = loadCart(cartId)) {
            is Cart.ExistingCart ->
                when (val rule = discount(cart.customer)) {
                    DiscountRule.GoldDiscount -> AppliedDiscount(cart.copy(amount = rule.apply(cart.amount)))
                    DiscountRule.NoDiscountRule -> NoDiscount
                }
            Cart.MissingCart -> NoDiscount
        }

    private fun loadCart(id: CartId): Cart =
        when {
            id.contains("gold") -> Cart.ExistingCart(id, CustomerId.GoldCustomer, 100.0)
            id.contains("normal") -> Cart.ExistingCart(id, CustomerId.NormalCustomer, 100.0)
            else -> Cart.MissingCart
        }

    private fun discount(customerId: CustomerId): DiscountRule =
        when (customerId) {
            CustomerId.GoldCustomer -> DiscountRule.GoldDiscount
            CustomerId.NormalCustomer -> DiscountRule.NoDiscountRule
        }
}