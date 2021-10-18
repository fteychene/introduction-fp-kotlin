
// Compose

fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C =
    { a: A -> f(g(a)) }

val fahrenheit2celsius: (Double) -> String =
    compose<Double, Double, String>(
        { b -> "$b degrees celsius" },
        { a -> (a - 32.0) * (5.0 / 9.0) }
    )

fahrenheit2celsius(68.0)

// Pipe

fun <A, B, C> ((A) -> B).pipe(f: (B) -> C): (A) -> C =
    { a: A -> f(this(a)) }

val fahrenheit2celsiusPipe: (Double) -> String =
    { a: Double -> (a - 32.0) * (5.0 / 9.0) }
        .pipe { b: Double -> "$b degrees celsius" }

fahrenheit2celsiusPipe(68.0)