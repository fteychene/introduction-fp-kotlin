package domain.cart

import arrow.core.Either
import arrow.core.filterOrElse

sealed class CartStoreException
data class DatabaseError(val cause: Throwable): CartStoreException()
data class CartNotFound(val id: CartId): CartStoreException()

interface CartEventStore {

    fun addEvent(event: CartEvent): Either<CartStoreException, CartEvent>

    fun loadEvents(cartId: CartId): Either<CartStoreException, List<CartEvent>>

    fun loadEvents(): Either<CartStoreException, List<CartEvent>>

    fun loadCartById(cartId: CartId): Either<CartStoreException, Cart> =
        loadEvents(cartId)
            .filterOrElse({ it.isNotEmpty() }) { CartNotFound(cartId) }
            .map { reload(it) }

}