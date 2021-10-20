
fun square(x: Int): Int = x * x

fun double(x: Int): Int = x * 2

fun printSquare(x: Int): String =
    "Square of $x is ${square(x)}"

fun printDouble(x: Int): String =
    "Double of $x is ${double(x)}"

printSquare(3)
printDouble(3)

// Function as value

fun printOperation(name: String, x: Int, op: (Int) -> Int): String =
    "$name of $x is ${op(x)}"


printOperation("Square", 3, ::square)
printOperation("Double", 3, ::double)


// Function could be returned

fun operation(name: String, op: (Int) -> Int): (Int) -> String =
    { value -> "$name of $value is ${op(value)}" }

val square2 = operation("Square", ::square)
val double2 = operation("Double", ::double)

square2(3)
double2(3)