package option

import functor.ForOption
import functor.Kind

sealed class Option<out T>: Kind<ForOption, T> {

    abstract fun isEmpty(): Boolean

    fun <A> fold(ifEmpty: () -> A, ifSome: (T) -> A) =
        when (this) {
            None -> ifEmpty()
            is Some -> ifSome(this.value)
        }

    fun <B> map(f: (T) -> B): Option<B> =
        flatMap { just(f(it)) }

    fun <B> flatMap(f: (T) -> Option<B>): Option<B> =
        when (this) {
            None -> None
            is Some -> f(this.value)
        }

    companion object {
        fun <T> just(v: T): Option<T> = Some(v)

        fun <T> fromNullable(v: T?): Option<T> = v?.let(::just) ?: None

        operator fun <T> invoke(v: T): Option<T> = just(v)
    }
}

data class Some<T>(val value: T) : Option<T>() {
    override fun isEmpty(): Boolean = true
}

object None : Option<Nothing>() {
    override fun isEmpty(): Boolean = true
}

fun <T> Option<T>.getOrElse(default: () -> T): T =
    fold(default) { it }
