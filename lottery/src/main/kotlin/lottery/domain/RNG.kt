package lottery.domain


interface RNG {
    fun nextInt(max: Int): Pair<Int, RNG>
}