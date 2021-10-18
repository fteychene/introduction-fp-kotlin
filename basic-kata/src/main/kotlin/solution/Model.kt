package solution

typealias CartId = String

sealed class CustomerId {
    object GoldCustomer : CustomerId()
    object NormalCustomer : CustomerId()
}

typealias Amount = Double

sealed class Cart {
    data class ExistingCart(var id: CartId, var customer: CustomerId, var amount: Amount): Cart()
    object MissingCart: Cart()
}

sealed class DiscountRule(val apply: (Amount) -> Amount) {
    object GoldDiscount: DiscountRule({it / 2})
    object NoDiscountRule: DiscountRule({it})
}

sealed class Discount
data class AppliedDiscount(val cart: Cart.ExistingCart): Discount()
object NoDiscount: Discount()


interface Storage<T> {
    fun flush(item: T)
}