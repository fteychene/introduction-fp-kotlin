

// Partial

fun <A, B, C> partial1(a: A, f: (A, B) -> C): (B) -> C =
    { b -> f(a, b) }

fun <A, B, C> partial2(b: B, f: (A, B) -> C): (A) -> C =
    { a -> f(a, b) }

// Curry

fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C =
    { a: A -> { b: B -> f(a, b) } }
