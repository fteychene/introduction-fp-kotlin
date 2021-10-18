package infra.adapter.cart

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import domain.cart.*
import org.postgresql.util.PGobject
import org.springframework.data.repository.CrudRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import java.time.ZonedDateTime
import javax.persistence.*

val objectMapper = ObjectMapper().registerKotlinModule()


fun CartEvent.toJson(): String =
    objectMapper.writeValueAsString(this)

fun String.toCartEvent(type: String): CartEvent =
    when (type) {
        "CartCreatedEvent" -> objectMapper.readValue(this, CartCreatedEvent::class.java)
        "OrderConfirmedEvent" -> objectMapper.readValue(this, OrderConfirmedEvent::class.java)
        "ProductSelectedEvent" -> objectMapper.readValue(this, ProductSelectedEvent::class.java)
        "ProductUnselectedEvent" -> objectMapper.readValue(this, ProductUnselectedEvent::class.java)
        else -> throw IllegalStateException("NOOOOOOOOOOOOOOOOOO, GOD  NOOOOOOOOOOOOOOOOOO")
    }


fun CartEvent.toEventType(): String =
    when (this) {
        is CartCreatedEvent -> "CartCreatedEvent"
        is OrderConfirmedEvent -> "OrderConfirmedEvent"
        is ProductSelectedEvent -> "ProductSelectedEvent"
        is ProductUnselectedEvent -> "ProductUnselectedEvent"
    }

@Entity
@Table(name = "events")
data class JpaCartEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val cartId: CartId,
    @Column(name = "event_type")
    val type: String,
    @Column(name = "created_at")
    val createAt: ZonedDateTime,
    val event: String
) {
    companion object {
        fun from(event: CartEvent): JpaCartEvent =
            JpaCartEvent(
                cartId = event.cartId,
                type = event.toEventType(),
                createAt = ZonedDateTime.now(),
                event = event.toJson()
            )
    }
}

interface JpaCartEventRepository: CrudRepository<JpaCartEvent, Int> {

    fun findByCartId(cartId: CartId): List<JpaCartEvent>
}

class CartEventRepository(val jpaRepository: JpaCartEventRepository): CartEventStore {

    override fun addEvent(event: CartEvent): Either<CartStoreException, CartEvent> =
        Either.catch {
            jpaRepository.save(JpaCartEvent.from(event)).let {
               it.event.toCartEvent(it.type)
           }
        }.mapLeft { DatabaseError(it) }


    override fun loadEvents(cartId: CartId): Either<CartStoreException, List<CartEvent>> =
        Either.catch {
            jpaRepository.findByCartId(cartId)
                .map { it.event.toCartEvent(it.type) }
        }.mapLeft { DatabaseError(it) }

    override fun loadEvents(): Either<CartStoreException, List<CartEvent>> =
        Either.catch {
            jpaRepository.findAll()
                .map { it.event.toCartEvent(it.type) }
        }.mapLeft { DatabaseError(it) }
}
