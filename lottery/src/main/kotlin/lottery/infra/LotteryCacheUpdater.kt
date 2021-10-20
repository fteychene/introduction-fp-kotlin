package lottery.infra

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lottery.domain.EventPort
import lottery.domain.LotteryService
import java.util.concurrent.Executors
import javax.annotation.PostConstruct

class LotteryCacheUpdater(
    val cache: LotteryAttendeeCache,
    val lotteryService: LotteryService,
    val eventPort: EventPort
) {

    tailrec suspend fun updateCache() {
        val (result, newState) = lotteryService.loadAttendees(eventPort)
            .run(cache.getCache())
        result.fold(
            { error -> println("Error updating attendee cache: $error") },
            { updated ->
                println("Attendees in cache $updated")
                cache.updateCache(newState)
            }
        )
        delay(5000L)
        updateCache()
    }

    @PostConstruct
    fun update() {
        Executors.newSingleThreadExecutor().submit {
            runBlocking { updateCache() }
        }
    }
}