// Side effects

object WithSideEffect {
    data class CreditCard(var amount: Double = 0.0) {
        fun charge(price: Double) {
            amount += price
        }
    }

    data class Coffee(val price: Double = 2.5)

    fun buyCoffee(cc: CreditCard): Coffee {
        val cup = Coffee()
        cc.charge(cup.price)
        return cup
    }
}

val sideCC = WithSideEffect.CreditCard()
WithSideEffect.buyCoffee(sideCC)
WithSideEffect.buyCoffee(sideCC)
sideCC


// Wihout side effect

object WihoutSideEffect {

    data class CreditCard(val amount: Double = 0.0)

    data class Coffee(val price: Float = 2.50F)

    data class Charge(val cc: CreditCard, val amount: Float) {
        fun combine(other: Charge): Charge =
                Charge(cc, amount + other.amount)

        fun apply(): CreditCard = // Side effect
            cc.copy(amount = cc.amount + amount)
    }


    fun buyCoffee(cc: CreditCard): Pair<Coffee, Charge> {
        val cup = Coffee()
        return Pair(cup, Charge(cc, cup.price))
    }

}

val cc = WihoutSideEffect.CreditCard()
val (coffee1, charge1) = WihoutSideEffect.buyCoffee(cc)
val (coffee2, charge2) = WihoutSideEffect.buyCoffee(cc)

charge1.combine(charge2).apply()
cc


// Total

fun divide(a: Double, b: Double): Double = a / b

5 / 4
5 / 0