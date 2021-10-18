package domain.cart

import arrow.core.*
import domain.cart.command.*


sealed class CommandHandlerException
data class CartAlreadyExist(val cartId: CartId) : CommandHandlerException()
data class InvalidFoodCartAggregate(val cartId: CartId, val cause: CartStoreException) : CommandHandlerException()
data class StoreException(val cause: CartStoreException) : CommandHandlerException()
data class AlreadyConfirmedCart(val cartId: CartId) : CommandHandlerException()
data class ProductDeselectionException(val message: String) : CommandHandlerException()

fun cartCommmandHandler(store: CartEventStore): (CartCommand) -> Either<CommandHandlerException, CartEvent> =
    { command ->
        when (command) {
            is CreateCartCommand -> handleCreateCartCommand(command, store)
            is SelectProductCommand -> handleSelectProductCommand(command)
            is UnselectProductCommand -> handleUnselectProductCommand(command, store)
            is ConfirmOrderCommand -> handleConfirmOrderCommand(command, store)
        }
    }

fun handleCreateCartCommand(
    command: CreateCartCommand,
    eventStore: CartEventStore
): Either<CommandHandlerException, CartCreatedEvent> =
    eventStore.loadCartById(command.cartId)
        .swap()
        .mapLeft { CartAlreadyExist(command.cartId) }
        .filterOrOther({ it is CartNotFound }) { StoreException(it) }
        .map { CartCreatedEvent(cartId = command.cartId) }

fun handleSelectProductCommand(command: SelectProductCommand): Either<CommandHandlerException, ProductSelectedEvent> =
    ProductSelectedEvent(
        cartId = command.cartId,
        productId = command.productId,
        quantity = command.quantity
    ).right()

fun handleUnselectProductCommand(
    command: UnselectProductCommand,
    eventStore: CartEventStore
): Either<CommandHandlerException, ProductUnselectedEvent> =
    eventStore.loadCartById(command.cartId)
        .mapLeft { InvalidFoodCartAggregate(command.cartId, it) }
        .filterOrElse({ it.products.containsKey(command.productId) }) { ProductDeselectionException("Cannot deselect a product which has not been selected for this Food Cart") }
        .filterOrElse({ it.products[command.productId]!! - command.quantity > 0 }) { ProductDeselectionException("Cannot deselect more products of ID [" + command.productId + "] than have been selected initially") }
        .map {
            ProductUnselectedEvent(
                cartId = command.cartId,
                productId = command.productId,
                quantity = command.quantity
            )
        }

fun handleConfirmOrderCommand(
    command: ConfirmOrderCommand,
    eventStore: CartEventStore
): Either<CommandHandlerException, OrderConfirmedEvent> =
    eventStore.loadCartById(command.cartId)
        .mapLeft { InvalidFoodCartAggregate(command.cartId, it) }
        .filterOrElse({ !it.confirmed }) { AlreadyConfirmedCart(command.cartId) }
        .map { OrderConfirmedEvent(cartId = command.cartId) }