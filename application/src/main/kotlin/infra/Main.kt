package infra

import domain.EventHandler
import domain.cart.CartEvent
import domain.cart.CartEventStore
import domain.catalog.ProductRepository
import domain.combine
import domain.open
import domain.read.CartProjection
import domain.read.OrderCounterProjection
import infra.adapter.cart.CartEventRepository
import infra.adapter.cart.JpaCartEventRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@SpringBootApplication
@EntityScan("domain.*", "infra.*") // Should not have to do that
class ShippingApplication

fun main(args: Array<String>) {
    runApplication<ShippingApplication>(*args)
}

@Component
class Configuration {

    @Bean
    fun cartEventStore(jpaRepository: JpaCartEventRepository): CartEventStore =
        CartEventRepository(jpaRepository)

    @Bean
    fun cartProjection(productRepository: ProductRepository): CartProjection =
        productRepository.findAll()
            .fold(
                { e -> throw IllegalStateException("Error inserting product at startup : $e") },
                { products ->
                    CartProjection(
                        prices = products.associate { it.id!! to it.price }
                    )
                }
            )

    @Bean
    fun orderProjection(): OrderCounterProjection =
        OrderCounterProjection()

    @Bean
    fun cartEventHandler(
        cartProjection: CartProjection,
        orderProjection: OrderCounterProjection
    ): EventHandler<CartEvent> =
        cartProjection::handleEvent.combine(orderProjection::handleEvent.open())
}