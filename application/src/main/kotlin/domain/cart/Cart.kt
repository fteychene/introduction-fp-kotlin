package domain.cart

import domain.catalog.ProductId

typealias CartId = String

data class Cart(
    val id: CartId,
    val products: Map<ProductId, Int> = mapOf(),
    val confirmed: Boolean = false
)


fun onCartCreatedEvent(event: CartCreatedEvent): Cart =
    Cart(id = event.cartId)

fun onProductSelectedEvent(cart: Cart, event: ProductSelectedEvent): Cart =
    cart.copy(products = cart.products + (event.productId to cart.products.getOrDefault(event.productId, 0) + event.quantity))

fun onProductUnselectedEvent(cart: Cart, event: ProductUnselectedEvent): Cart =
    cart.copy(products = cart.products + (event.productId to cart.products.getOrDefault(event.productId, 0) - event.quantity))

fun onOrderConfirmedEvent(cart: Cart, _event : OrderConfirmedEvent): Cart =
    cart.copy(confirmed = true)

fun reload(events: List<CartEvent>): Cart =
    events.fold(Cart(id = "")) { foodCart, event ->
        when (event) {
            is CartCreatedEvent -> onCartCreatedEvent(event)
            is OrderConfirmedEvent -> onOrderConfirmedEvent(foodCart, event)
            is ProductSelectedEvent -> onProductSelectedEvent(foodCart, event)
            is ProductUnselectedEvent -> onProductUnselectedEvent(foodCart, event)
        }
    }