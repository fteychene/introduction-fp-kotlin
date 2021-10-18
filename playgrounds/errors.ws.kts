fun mean(xs: List<Double>): Double =
    if (xs.isEmpty())
        throw ArithmeticException("mean of emtpy list!")
    else xs.sum() / xs.size

mean(listOf(3.0, 4.5, 5.3, 7.9))
mean(listOf())