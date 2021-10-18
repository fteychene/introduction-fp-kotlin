package base

typealias Ip = String
typealias User = String
typealias Stat = Pair<User, Int>


interface SecurityPort {

    fun authorize(ip: Ip): User?
}

interface StatRepository {

    fun track(user: User): Stat?

}

class Tracker(val security: SecurityPort, val stats: StatRepository) {

    fun track(ip: Ip): Stat? {
        val user = security.authorize(ip)
        if (user != null) {
            return stats.track(user)
        }
        return null
    }
}

fun main() {
    val security = object : SecurityPort {
        override fun authorize(ip: Ip): User = "Francois"
    }
    val stats = object : StatRepository {
        override fun track(user: User): Stat = user to 1
    }

    val tracker = Tracker(security, stats)
    println(tracker.track("192.168.1.6"))
    println(tracker.track("192.168.1.6"))
    println(tracker.track("192.168.1.6"))
}