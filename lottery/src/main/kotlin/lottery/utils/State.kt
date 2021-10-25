package lottery.utils

import arrow.core.Either

data class State<S, out A>(val run: suspend (S) -> Pair<A, S>) {

    fun <B> map(f: (A) -> B): State<S, B> =
        flatMap { a -> unit(f(a)) }

    fun <B> flatMap(f: (A) -> State<S, B>): State<S, B> =
        State { s: S ->
            val (a: A, s2: S) = this.run(s)
            f(a).run(s2)
        }



    companion object {
        fun <S, A> unit(a: A): State<S, A> =
            State { s: S -> a to s }

        fun <S, A, B, C> map2(
            ra: State<S, A>,
            rb: State<S, B>,
            f: (A, B) -> C
        ): State<S, C> =
            ra.flatMap { a ->
                rb.map { b ->
                    f(a, b)
                }
            }

        fun <S, A, B, C> map3(
            ra: State<S, A>,
            rb: State<S, B>,
            rc: State<S, C>,
            f: (A, B, C) -> C
        ): State<S, C> =
            ra.flatMap { a ->
                rb.flatMap { b ->
                    rc.map { c ->
                        f(a, b, c)
                    }
                }
            }
    }
}

inline fun <A, B, S, C> Either<A, B>.traverseState(crossinline fa: (B) -> State<S, C>): State<S, Either<A, C>> =
    State { s ->
        when (this) {
            is Either.Right -> fa(this.value).map { Either.Right(it) }
            is Either.Left -> State.unit(this)
        }.run(s)
    }