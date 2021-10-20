// Side effects

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

val cc = CreditCard()
buyCoffee(cc)
buyCoffee(cc)
cc


// Wihout side effect


//data class CreditCard(val amount: Double = 0.0)
//
//data class Coffee(val price: Double = 2.50)
//
//data class Charge(val cc: CreditCard, val amount: Double) {
//    fun combine(other: Charge): Charge =
//        Charge(cc, amount + other.amount)
//
//    fun chargeCC() =
//        cc.copy(amount = cc.amount + amount)
//}
//
//
//fun buyCoffee(cc: CreditCard): Pair<Coffee, Charge> {
//    val cup = Coffee()
//    return Pair(cup, Charge(cc, cup.price))
//}
//
//
//val cc = CreditCard()
//buyCoffee(cc)
//buyCoffee(cc)
//
//fun buyCoffees(
//    cc: CreditCard,
//    n: Int
//): Pair<List<Coffee>, Charge> {
//
//    val purchases: List<Pair<Coffee, Charge>> =
//        List(n) { buyCoffee(cc) }
//
//    val (coffees, charges) = purchases.unzip()
//
//    return Pair(
//        coffees,
//        charges.reduce { c1, c2 -> c1.combine(c2) }
//    )
//}
//
//buyCoffees(cc, 4)



// Total

//fun divide(a: Int, b: Int) = a / b
//
//divide(5, 4)
//divide(5, 0)