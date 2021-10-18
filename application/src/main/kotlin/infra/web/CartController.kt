package infra.web

import arrow.core.andThen
import arrow.core.flatMap
import domain.EventHandler
import domain.cart.*
import domain.cart.command.ConfirmOrderCommand
import domain.cart.command.CreateCartCommand
import domain.cart.command.SelectProductCommand
import domain.cart.command.UnselectProductCommand
import domain.catalog.ProductNotFound
import domain.catalog.ProductRepository
import domain.read.Cart
import domain.read.CartProjection
import infra.adapter.catalog.SpringDataProductRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart")
class CartController(
    val cartEventStore: CartEventStore,
    val cartEventHandler: EventHandler<CartEvent>,
    val cartProjection: CartProjection,
    val productRepository: ProductRepository
) {

    val handler = cartCommmandHandler(cartEventStore)
        .andThen { it.flatMap { events -> cartEventStore.addEvent(events) } }
        .andThen { it.map { event -> cartEventHandler(event); event } }

    @PostMapping("/claim/{cartId}")
    fun claim(@PathVariable cartId: CartId): ResponseEntity<CartEvent> =
        handler(CreateCartCommand(cartId))
            .fold(
                { e ->
                    when (e) {
                        is CartAlreadyExist -> ResponseEntity.status(HttpStatus.CONFLICT).build()
                        else -> {
                            println("Error : $e")
                            ResponseEntity.internalServerError().build()
                        }
                    }
                },
                {
                    ResponseEntity.status(HttpStatus.CREATED).body(it)
                }
            )

    @PostMapping("/select")
    fun select(@RequestBody selectCommand: SelectProductCommand): ResponseEntity<CartEvent> =
        productRepository.findById(selectCommand.productId)
            .flatMap { handler(selectCommand) }
            .fold(
                { e ->
                    when (e) {
                        is ProductNotFound -> ResponseEntity.badRequest().build()
                        is InvalidFoodCartAggregate -> ResponseEntity.badRequest().build()
                        else -> {
                            println("Error : $e")
                            ResponseEntity.internalServerError().build()
                        }
                    }
                },
                {
                    ResponseEntity.ok(it)
                }
            )

    @PostMapping("/unselect")
    fun unselect(@RequestBody unselectCommand: UnselectProductCommand): ResponseEntity<CartEvent> =
        productRepository.findById(unselectCommand.productId)
            .flatMap { handler(unselectCommand) }
            .fold(
                { e ->
                    when (e) {
                        is ProductNotFound -> ResponseEntity.badRequest().build()
                        is InvalidFoodCartAggregate -> ResponseEntity.badRequest().build()
                        else -> {
                            println("Error : $e")
                            ResponseEntity.internalServerError().build()
                        }
                    }
                },
                {
                    ResponseEntity.ok(it)
                }
            )

    @PostMapping("/confirm/{cartId}")
    fun confirm(@PathVariable cartId: CartId): ResponseEntity<CartEvent> =
        handler(ConfirmOrderCommand(cartId))
            .fold(
                { e ->
                    when (e) {
                        is InvalidFoodCartAggregate -> ResponseEntity.badRequest().build()
                        is AlreadyConfirmedCart -> ResponseEntity.status(HttpStatus.CONFLICT).build()
                        else -> {
                            println("Error : $e")
                            ResponseEntity.internalServerError().build()
                        }
                    }
                },
                {
                    ResponseEntity.ok(it)
                }
            )

    @GetMapping("/{cartId}")
    fun getCartInformation(@PathVariable cartId: CartId): ResponseEntity<Cart> =
        cartProjection.getProjection(cartId)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @GetMapping("/events")
    fun getEvents(): ResponseEntity<List<CartEvent>> =
        cartEventStore.loadEvents()
            .fold(
                { e ->
                    println("Error : $e")
                    ResponseEntity.internalServerError().build()
                },
                {
                    ResponseEntity.ok(it)
                }
            )

}