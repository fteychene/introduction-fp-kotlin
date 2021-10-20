package lottery.domain

import arrow.core.Either

sealed class EventPortError: LotteryError
data class DatasourceIssue(val cause: Throwable): EventPortError()

interface EventPort {
    fun getEvents(): Either<EventPortError, List<Event>>

    fun getAttendees(eventId: EventId): Either<EventPortError, List<Attendee>>
}
