package option

typealias Ip = String
typealias User = String
typealias Stat = Pair<User, Int>


interface SecurityPort {

    fun authorize(ip: Ip): Option<User>
}

interface StatRepository {

    fun track(user: User): Option<Stat>

}

class Tracker(val security: SecurityPort, val stats: StatRepository) {

    fun track(ip: Ip): Option<Stat> =
        security.authorize(ip)
            .flatMap { stats.track(it) }
}

fun main() {
    val security = object : SecurityPort {
        override fun authorize(ip: Ip): Option<User> = Option("Francois")
    }
    val stats = object : StatRepository {
        override fun track(user: User): Option<Stat> = Option(user to 1)
    }

    val tracker = Tracker(security, stats)
    println(tracker.track("192.168.1.6"))
    println(tracker.track("192.168.1.6"))
    println(tracker.track("192.168.1.6"))
}