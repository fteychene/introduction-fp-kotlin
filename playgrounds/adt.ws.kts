
// Sum type

enum class Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAYm
    SUNDAY
}

// Product type

data class Product(
    val id: java.util.UUID,
    val amount: Double,
    val products: List<String>
)

// ADT

sealed class CartEvent(open val cartId: CartId)

data class CartCreatedEvent(override val cartId: CartId): CartEvent(cartId)

data class ProductSelectedEvent(
    override val cartId: CartId,
    val productId: ProductId,
    val quantity: Int
): CartEvent(cartId)

data class ProductUnselectedEvent(
    override val cartId: CartId,
    val productId: ProductId,
    val quantity: Int
): CartEvent(cartId)

data class OrderConfirmedEvent(override val cartId: CartId): CartEvent(cartId)


when()