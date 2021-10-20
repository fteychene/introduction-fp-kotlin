fun add(x: Int, y: Int): Int = x + y

// Partial

fun <A, B, C> partial1(a: A, f: (A, B) -> C): (B) -> C =
    { b -> f(a, b) }

fun <A, B, C> partial2(b: B, f: (A, B) -> C): (A) -> C =
    { a -> f(a, b) }

val add3 = partial1(3, ::add)

add3(4)

// Curry

fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C =
    { a: A -> { b: B -> f(a, b) } }

val curried = curry(::add)

curried(3)(4)

val add3ByCurry = curried(3)

add3ByCurry(4)



