
fun fahrenheitToCelsius(x: Double): Double =
    (x - 32.0) * (5.0 / 9.0)

fun printCelsius(x: Double): String =
    "$x degrees celsius"


// Compose

//fun <A, B, C> compose(g: (B) -> C, f: (A) -> B): (A) -> C =
//    { this(f(it)) }

fun <A, B, C> ((B) -> C).compose(f: (A) -> B): (A) -> C =
    { this(f(it)) }

val fahrenheightToCelsius2 = ::printCelsius.compose(::fahrenheitToCelsius)

fahrenheightToCelsius2(68.0)

// Pipe

//fun <A, B, C> pipe(f: (A) -> B, g: (B) -> C): (A) -> C =
//    { g(this(it))}

fun <A, B, C> ((A) -> B).pipe(g: (B) -> C): (A) -> C =
    { g(this(it))}


val fahrenheightToCelsius3 = ::fahrenheitToCelsius.pipe(::printCelsius)

fahrenheightToCelsius3(68.0)