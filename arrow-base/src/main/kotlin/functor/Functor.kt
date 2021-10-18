package functor

import option.None
import option.Option
import option.Some

interface Kind<out F, out A>

interface Functor<F> {
    fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B>
}

class ForOption private constructor() {
    companion object
}


inline fun <A> Kind<ForOption, A>.fix(): Option<A> =
    this as Option<A>


val optionFunctor = object : Functor<ForOption> {
    override fun <A, B> map(fa: Kind<ForOption, A>, f: (A) -> B): Kind<ForOption, B> =
        when (val x = fa.fix()) {
            None -> None
            is Some -> Some(f(x.value))
        }
}





