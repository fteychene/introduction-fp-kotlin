package infra.adapter.cart

import arrow.core.Either
import arrow.core.right
import domain.cart.*

class InMemoryEventStore: CartEventStore {
    private val datas: MutableMap<CartId, List<CartEvent>> = mutableMapOf()

    override fun addEvent(event: CartEvent): Either<CartStoreException, CartEvent> {
        datas[event.cartId] = datas.getOrDefault(event.cartId, listOf()) + event
        return event.right()
    }

    override fun loadEvents(cartId: CartId): Either<CartStoreException, List<CartEvent>> =
        datas.getOrDefault(cartId, listOf()).right()

    override fun loadEvents(): Either<CartStoreException, List<CartEvent>> =
        datas.values.flatten().right()
}