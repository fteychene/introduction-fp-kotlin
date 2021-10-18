package domain

typealias EventHandler<EVENT> = (EVENT) -> Unit // Send event to read model is a fire & forget action

fun <E> EventHandler<E>.combine(other: EventHandler<E>): EventHandler<E> = { event ->
    this(event)
    other(event)
}

inline fun <ORIGIN, reified TARGET: ORIGIN> EventHandler<TARGET>.open(): EventHandler<ORIGIN> = { e: ORIGIN ->
    when(e) {
        is TARGET -> this(e)
        else -> {}
    }
}