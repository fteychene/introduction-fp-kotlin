package lottery.infra

import lottery.domain.EventPort
import lottery.domain.LotteryService
import lottery.domain.RNG
import lottery.infra.adapter.DatabaseEventRepository
import lottery.infra.adapter.JpaAttendeeRepository
import lottery.infra.adapter.JpaEventRepository
import lottery.infra.adapter.KotlinRNG
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class Components {

    @Bean
    fun rng(): RNG = KotlinRNG

    @Bean
    fun lotteryService() = LotteryService()

    @Bean
    fun lotteryAttendeeCache() = LotteryAttendeeCache()

    @Bean
    fun lotteryCacheUpdater(
        lotteryCache: LotteryAttendeeCache,
        lotteryService: LotteryService,
        eventPort: EventPort
    ) = LotteryCacheUpdater(lotteryCache, lotteryService, eventPort)

    @Bean
    fun eventPort(
        jpaEventRepository: JpaEventRepository,
        jpaAttendeeRepository: JpaAttendeeRepository
    ) = DatabaseEventRepository(jpaEventRepository, jpaAttendeeRepository)


}