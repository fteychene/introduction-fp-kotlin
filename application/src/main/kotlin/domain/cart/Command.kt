package domain.cart.command

import domain.cart.CartId
import domain.catalog.ProductId

sealed class CartCommand(open val cartId: CartId)

data class CreateCartCommand(override val cartId: CartId): CartCommand(cartId)

data class SelectProductCommand(
    override val cartId: CartId,
    val productId: ProductId,
    val quantity: Int
): CartCommand(cartId)

data class UnselectProductCommand(
    override val cartId: CartId,
    val productId: ProductId,
    val quantity: Int
): CartCommand(cartId)

data class ConfirmOrderCommand(override val cartId: CartId): CartCommand(cartId)
