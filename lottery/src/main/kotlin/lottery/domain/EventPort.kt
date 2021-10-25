package lottery.domain

import arrow.core.Either

sealed class EventPortError: LotteryError
data class DatasourceIssue(val cause: Throwable): EventPortError()

interface EventPort {
    suspend fun getEvents(): Either<EventPortError, List<Event>>

    suspend fun getAttendees(eventId: EventId): Either<EventPortError, List<Attendee>>
}
