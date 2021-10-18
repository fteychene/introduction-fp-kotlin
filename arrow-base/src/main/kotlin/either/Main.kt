package either

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

    fun track(user: User): Either<StatError, Stat>

}

class Tracker(val security: SecurityPort, val stats: StatRepository) {

    fun track(ip: Ip): Either<TrackerError, Stat> =
        security.authorize(ip)
            .flatMap { stats.track(it) }
}

fun main() {
    val security = object : SecurityPort {
        override fun authorize(ip: Ip): Either<SecurityError, User> = Either.left(UserNotFoundError(ip))
    }
    val stats = object : StatRepository {
        var values = mutableMapOf<User, Int>()
        override fun track(user: User): Either<StatError, Stat> = Either.just(user to 1)
    }

    val tracker = Tracker(security, stats)
    println(tracker.track("192.168.1.6"))
    println(tracker.track("192.168.1.6"))
    println(tracker.track("192.168.1.6"))
}