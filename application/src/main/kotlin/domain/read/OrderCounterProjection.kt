package domain.read

import domain.cart.OrderConfirmedEvent

class OrderCounterProjection{
    private var counter = 0

    fun handleEvent(_order: OrderConfirmedEvent) {
        counter += 1
    }

    fun getProjection(): Int = counter
}