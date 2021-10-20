package lottery.domain

import arrow.core.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class LotteryServiceTest {

    @Test
    fun `draw should pick an attendee based on RNG`() {
        val attendees = listOf(
            Attendee(firstName = "Test", lastName = "0", email = "test.0@gmail.com"),
            Attendee(firstName = "Test", lastName = "1", email = "test.1@gmail.com"),
            Attendee(firstName = "Test", lastName = "2", email = "test.2@gmail.com"),
            Attendee(firstName = "Test", lastName = "3", email = "test.3@gmail.com")
        )
        val lottery = LotteryService()

        assertEquals(
            listOf(Attendee(firstName = "Test", lastName = "3", email = "test.3@gmail.com")),
            lottery.draw(attendees, 1).run(object : RNG {
                override fun nextInt(max: Int): Pair<Int, RNG> = 3 to this
            }).first
        )
        assertEquals(
            listOf(Attendee(firstName = "Test", lastName = "2", email = "test.2@gmail.com")),
            lottery.draw(attendees, 1).run(object : RNG {
                override fun nextInt(max: Int): Pair<Int, RNG> = 2 to this
            }).first
        )
    }

    @Test
    fun `draw should return an empty list on 0 asked`() {
        val attendees = listOf(
            Attendee(firstName = "Test", lastName = "0", email = "test.0@gmail.com"),
            Attendee(firstName = "Test", lastName = "1", email = "test.1@gmail.com"),
            Attendee(firstName = "Test", lastName = "2", email = "test.2@gmail.com"),
            Attendee(firstName = "Test", lastName = "3", email = "test.3@gmail.com")
        )
        val lottery = LotteryService()

        assertEquals(
            listOf<Attendee>(),
            lottery.draw(attendees, 0).run(object : RNG {
                override fun nextInt(max: Int): Pair<Int, RNG> = 3 to this
            }).first
        )
    }

    @Test
    fun `draw should return multiple attendees based on RNG`() {
        val attendees = listOf(
            Attendee(firstName = "Test", lastName = "0", email = "test.0@gmail.com"),
            Attendee(firstName = "Test", lastName = "1", email = "test.1@gmail.com"),
            Attendee(firstName = "Test", lastName = "2", email = "test.2@gmail.com"),
            Attendee(firstName = "Test", lastName = "3", email = "test.3@gmail.com")
        )
        val lottery = LotteryService()

        val rng = object : RNG {
            var called = 0
            override fun nextInt(max: Int): Pair<Int, RNG> {
                called += 1
                return when (called) {
                    1 -> 0
                    2 -> 2
                    3 -> 3
                    else -> fail("RNG should not called more than 3 time")
                } to this
            }
        }
        assertEquals(
            listOf(
                Attendee(firstName = "Test", lastName = "0", email = "test.0@gmail.com"),
                Attendee(firstName = "Test", lastName = "2", email = "test.2@gmail.com"),
                Attendee(firstName = "Test", lastName = "3", email = "test.3@gmail.com")
            ) to rng,
            lottery.draw(attendees, 3).run(rng)
        )
        assertEquals(3, rng.called)
    }

    @Test
    fun `updateAttendees should update cache`() {
        val lottery = LotteryService()

        val attendees = listOf(
            Attendee(firstName = "Test", lastName = "0", email = "test.0@gmail.com"),
            Attendee(firstName = "Test", lastName = "1", email = "test.1@gmail.com"),
            Attendee(firstName = "Test", lastName = "2", email = "test.2@gmail.com"),
            Attendee(firstName = "Test", lastName = "3", email = "test.3@gmail.com")
        )

        val stateUpdate = lottery.updateAttendees(attendees)

        assertEquals(
            attendees.size to Some(AttendeeCache(attendees)),
            stateUpdate.run(None)
        )
        assertEquals(
            attendees.size to Some(AttendeeCache(attendees)),
            stateUpdate.run(Some(AttendeeCache(listOf(Attendee(firstName = "Test", lastName = "0", email = "test.0@gmail.com")))))
        )
    }

    @Test
    fun `loadAttendees should update state`() {
        val attendees = listOf(
            Attendee(firstName = "Test", lastName = "0", email = "test.0@gmail.com"),
            Attendee(firstName = "Test", lastName = "1", email = "test.1@gmail.com"),
            Attendee(firstName = "Test", lastName = "2", email = "test.2@gmail.com"),
            Attendee(firstName = "Test", lastName = "3", email = "test.3@gmail.com")
        )
        val eventPort = object: EventPort {
            override fun getEvents(): Either<EventPortError, List<Event>> =
                listOf(Event(id = 1, start = ZonedDateTime.now())).right()

            override fun getAttendees(eventId: EventId): Either<EventPortError, List<Attendee>> =
                attendees.right()
        }

        val lottery = LotteryService()

        assertEquals(
            attendees.size.right() to Some(AttendeeCache(attendees)),
            lottery.loadAttendees(eventPort).run(None)
        )
    }

    @Test
    fun `loadAttendees should fail with NoEventAvailable`() {
        val eventPort = object: EventPort {
            override fun getEvents(): Either<EventPortError, List<Event>> =
                listOf<Event>().right()

            override fun getAttendees(eventId: EventId): Either<EventPortError, List<Attendee>> =
                fail("Should not have called EventPort.getAttendees")
        }

        val lottery = LotteryService()

        val state = Some(AttendeeCache(attendee = listOf()))

        assertEquals(
            NoEventAvailable.left() to state,
            lottery.loadAttendees(eventPort).run(state)
        )
    }

    @Test
    fun `loadAttendees should fail with EventPortError on getEvent`() {
        val error = DatasourceIssue(IllegalStateException("Manual"))
        val eventPort = object: EventPort {
            override fun getEvents(): Either<EventPortError, List<Event>> =
                error.left()

            override fun getAttendees(eventId: EventId): Either<EventPortError, List<Attendee>> =
                fail("Should not have called EventPort.getAttendees")
        }

        val lottery = LotteryService()

        val state = Some(AttendeeCache(attendee = listOf()))

        assertEquals(
            error.left() to state,
            lottery.loadAttendees(eventPort).run(state)
        )
    }

    @Test
    fun `loadAttendees should fail with EventPortError on getAttendees`() {
        val error = DatasourceIssue(IllegalStateException("Manual"))
        val eventPort = object: EventPort {
            override fun getEvents(): Either<EventPortError, List<Event>> =
                listOf(Event(id = 1, start = ZonedDateTime.now())).right()

            override fun getAttendees(eventId: EventId): Either<EventPortError, List<Attendee>> =
                error.left()
        }

        val lottery = LotteryService()

        val state = Some(AttendeeCache(attendee = listOf()))

        assertEquals(
            error.left() to state,
            lottery.loadAttendees(eventPort).run(state)
        )
    }
}