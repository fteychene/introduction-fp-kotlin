package lottery.infra.adapter

import lottery.domain.RNG
import kotlin.random.Random

object KotlinRNG: RNG {
    override fun nextInt(max: Int): Pair<Int, RNG> =
        Random.nextInt(max) to this
}