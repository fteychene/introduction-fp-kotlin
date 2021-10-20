package lottery.domain

import arrow.core.*
import lottery.utils.State
import lottery.utils.traverseState

sealed interface LotteryError

sealed class LotteryServiceError : LotteryError
object NoEventAvailable : LotteryServiceError()

class LotteryService {

    fun draw(attendees: List<Attendee>, n: Int): State<RNG, List<Attendee>> =
        when {
            n > 0 -> State<RNG, List<Attendee>> { rng ->
                val (index, updated) = rng.nextInt(attendees.size)
                listOf(attendees[index]) to updated
            }.flatMap { result ->
                draw(attendees, n - 1).map { result + it }
            }
            else -> State.unit(listOf())
        }

    fun updateAttendees(attendees: List<Attendee>): State<Option<AttendeeCache>, Int> =
        State { attendees.size to Some(AttendeeCache(attendees)) }

    fun loadAttendees(eventPort: EventPort): State<Option<AttendeeCache>, Either<LotteryError, Int>> =
        State { cache ->
            eventPort.getEvents()
                .filterOrElse({ it.isNotEmpty() }) { NoEventAvailable }
                .map { it.sortedBy { it.start }.let { println(it); it} .first() }
                .flatMap { eventPort.getAttendees(it.id) }
                .traverseState(this::updateAttendees)
                .run(cache)
        }
}

