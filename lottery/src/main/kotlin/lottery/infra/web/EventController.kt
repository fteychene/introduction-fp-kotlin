package lottery.infra.web

import lottery.domain.EventId
import lottery.infra.adapter.DatabaseAttendee
import lottery.infra.adapter.DataseEvent
import lottery.infra.adapter.JpaAttendeeRepository
import lottery.infra.adapter.JpaEventRepository
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime
import javax.transaction.Transactional

@RestController
@RequestMapping("/events")
class EventController(val eventRepository: JpaEventRepository, val attendeeRepository: JpaAttendeeRepository) {

    data class ApiAttendees(
        val firstName: String,
        val lastName: String,
        val email: String
    )

    data class ApiEvent(
        val id: EventId?,
        val name: String,
        val start: ZonedDateTime,
        val attendees: List<ApiAttendees>
    )

    fun ApiEvent.databaseEvent(): DataseEvent =
        DataseEvent(
            id = id,
            name = name,
            start = start
        )

    fun ApiEvent.databaseAttendees(eventId: EventId): List<DatabaseAttendee> =
        attendees.map {
            DatabaseAttendee(
                firstName = it.firstName,
                lastName = it.lastName,
                mail = it.email,
                event = DataseEvent(id = eventId, name= name, start = start)
            )
        }

    @PostMapping("/")
    @Transactional
    fun upsertEvent(@RequestBody event: ApiEvent): EventId {
        val savedEvent = eventRepository.save(event.databaseEvent())
        attendeeRepository.deleteByEvent(savedEvent)
        attendeeRepository.saveAll(event.databaseAttendees(savedEvent.id!!))
        return savedEvent.id!!
    }

    @DeleteMapping("/{eventId}")
    @Transactional
    fun deleteEvent(@PathVariable eventId: EventId) {
        attendeeRepository.deleteByEvent(DataseEvent(id = eventId, name= "", start = ZonedDateTime.now()))
        eventRepository.deleteById(eventId)
    }

}