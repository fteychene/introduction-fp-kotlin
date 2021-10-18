package state

import either.Either

typealias Ip = String
typealias User = String
typealias Stat = Pair<User, Int>

sealed interface TrackerError

sealed class SecurityError : TrackerError
data class UserNotFoundError(val ip: Ip) : SecurityError()

sealed class StatError : TrackerError
object DatabaseError : StatError()


interface SecurityPort {

    fun authorize(ip: Ip): Either<SecurityError, User>
}

interface StatRepository {

    fun inc(user: User): Either<StatError, Pair<StatRepository, Stat>>
}

class Tracker(val security: SecurityPort) {

    fun track(ip: Ip): State<StatRepository, Either<TrackerError, Stat>> =
        State { stats ->
            security.authorize(ip)
                .fold(
                    { e -> Either.left(e) to stats },
                    { user -> increment(user).run(stats) }
                )
        }

    fun increment(user: User): State<StatRepository, Either<StatError, Stat>> =
        State { statRepository ->
            statRepository.inc(user).fold(
                { e -> Either.left(e) to statRepository },
                { (updated: StatRepository, stat) -> Either.just(stat) to updated }
            )
        }

}

data class InMemoryStatRepository(private val values: Map<User, Int> = mapOf()) : StatRepository {
    override fun inc(user: User): Either<StatError, Pair<StatRepository, Stat>> =
        copy(values + (user to (values[user] ?: 0) + 1)).let { updated ->
            Either.just(updated to (user to updated.values[user]!!))
        }

}


fun main() {
    val security = object : SecurityPort {
        override fun authorize(ip: Ip): Either<SecurityError, User> = Either.just("Francois")
    }
    val statRepository = InMemoryStatRepository()

    println(statRepository)

    val program = Tracker(security).track("192.168.1.6")

    val (result1, newState1) = program.run(statRepository)
    println(result1)
    println(statRepository)
    println(newState1)
    val (result2, newState2) = program.run(newState1)
    println(result2)
    println(newState2)
    val (result3, newState3) = program.run(newState2)
    println(result3)
    println(newState3)

}