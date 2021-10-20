package lottery.infra

import arrow.core.None
import arrow.core.Option
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import lottery.domain.AttendeeCache
import java.util.concurrent.Executors

class LotteryAttendeeCache(
    private var cache: Option<AttendeeCache> = None
) {

    val cacheContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    suspend fun updateCache(newState: Option<AttendeeCache>) = withContext(cacheContext) {
        cache = newState
    }

    suspend fun getCache(): Option<AttendeeCache> = withContext(cacheContext) {
        cache
    }

}