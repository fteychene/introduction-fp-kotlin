package lottery.infra.web

import arrow.core.*
import arrow.core.Validated.Companion.invalidNel
import kotlinx.coroutines.runBlocking
import lottery.domain.Attendee
import lottery.domain.AttendeeCache
import lottery.domain.LotteryService
import lottery.domain.RNG
import lottery.infra.LotteryAttendeeCache
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LotteryController(
    val lotteryAttendeeCache: LotteryAttendeeCache,
    val lotteryService: LotteryService,
    val rng: RNG
) {

    @GetMapping("/winners")
    @ResponseBody
    fun winners(@RequestParam("nb") nb: Int): ResponseEntity<Any> = runBlocking {
        nb.isPositive()
            .zip(lotteryAttendeeCache.getCache().validateExists())
            { nb, cache ->
                ResponseEntity.ok(
                    lotteryService.draw(cache.attendee, nb)
                        .run(rng)
                        .first
                )
            }.fold(
                { errors ->
                    val status = if (errors.contains(NoEventInCache)) HttpStatus.SERVICE_UNAVAILABLE
                    else HttpStatus.BAD_REQUEST
                    ResponseEntity
                        .status(status)
                        .body(errors.map {
                            when(it) {
                                NoEventInCache -> "No event available"
                                is NonPositiveWinnerNumber -> "Invalid winner number asked"
                            }
                        })
                },
                { ResponseEntity.ok(it) }
            )
    }

    sealed class WinnersError(val errors: List<WinnerValidationError>)

    sealed class WinnerValidationError
    data class NonPositiveWinnerNumber(val nb: Int) : WinnerValidationError()
    object NoEventInCache : WinnerValidationError()

    fun Int.isPositive(): ValidatedNel<WinnerValidationError, Int> =
        if (this > 0) this.validNel()
        else NonPositiveWinnerNumber(this).invalidNel()

    fun Option<AttendeeCache>.validateExists(): ValidatedNel<WinnerValidationError, AttendeeCache> =
        fold(
            { NoEventInCache.invalidNel() },
            { it.valid() }
        )
}