package either

sealed class Either<out E, out A> {

    fun <B> map(f: (A) -> B): Either<E, B> =
        flatMap { just(f(it)) }

    fun <T> fold(onLeft: (E) -> T, onRight: (A) -> T): T =
        when(this) {
            is Left -> onLeft(this.error)
            is Right -> onRight(this.right)
        }

    companion object {
        fun <A> just(v: A): Either<Nothing, A> = right(v)

        fun <A> right(v: A): Either<Nothing, A> = Right(v)

        fun <E> left(v: E): Either<E, Nothing> = Left(v)
    }
}

data class Right<A>(val right: A): Either<Nothing, A>()
data class Left<E>(val error: E): Either<E, Nothing>()

fun <E, A, B> Either<E,A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> =
    when(this) {
        is Left -> this
        is Right -> f(this.right)
    }