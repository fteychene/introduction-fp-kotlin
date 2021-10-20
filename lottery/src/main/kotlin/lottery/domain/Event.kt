package lottery.domain

import java.time.ZonedDateTime

typealias EventId = Int

data class Event(
    val id: EventId,
    val start: ZonedDateTime
)
