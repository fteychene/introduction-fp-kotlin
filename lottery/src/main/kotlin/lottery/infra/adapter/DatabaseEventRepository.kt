package lottery.infra.adapter

import arrow.core.Either
import arrow.core.Option
import arrow.core.flatMap
import arrow.core.traverseEither
import lottery.domain.*
import org.springframework.data.repository.CrudRepository
import java.time.ZonedDateTime
import javax.persistence.*

@Entity
data class DataseEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: EventId? = null,
    val name: String,
    val start: ZonedDateTime
)

@Entity
data class DatabaseAttendee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val mail: String,
    @ManyToOne
    val event: DataseEvent
)

interface JpaEventRepository : CrudRepository<DataseEvent, Int>

interface JpaAttendeeRepository : CrudRepository<DatabaseAttendee, Int> {
    fun findByEvent(event: DataseEvent): List<DatabaseAttendee>

    fun deleteByEvent(event: DataseEvent)
}

class DatabaseEventRepository(
    val eventRepository: JpaEventRepository,
    val attendeeRepository: JpaAttendeeRepository
) : EventPort {

    override fun getEvents(): Either<EventPortError, List<Event>> =
        Either.catch {
            eventRepository
                .findAll()
        }.flatMap { events ->
            events.traverseEither { event ->
                Option.fromNullable(event.id).toEither { IllegalStateException("Event from database $event has no id") }
                    .map { id ->
                        Event(
                            id = id,
                            start = event.start
                        )
                    }
            }
        }.mapLeft { DatasourceIssue(it) }

    override fun getAttendees(eventId: EventId): Either<EventPortError, List<Attendee>> =
        Either.catch {
            attendeeRepository.findByEvent(DataseEvent(id = eventId, name = "", start = ZonedDateTime.now()))
                .map {
                    Attendee(
                        firstName = it.firstName,
                        lastName = it.lastName,
                        email = it.mail
                    )
                }
        }.mapLeft { DatasourceIssue(it) }
}