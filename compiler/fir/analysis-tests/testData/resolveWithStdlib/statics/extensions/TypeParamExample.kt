// TODO: abstract and open statics are not yet implemented, so this example is not finished
open class A {
    static {
        fun foo() {}
    }
}

class B : A() {
    static {
        override fun foo() {}
    }
}

class C : A() {
    static {
        override fun foo() {}
    }
}

fun <T : A> test(block: T.static.() -> Unit) {
    T.block()
}

fun main() {
    test<A> {
        foo()
    }

    test<B> {
        foo()
    }

    test<C> {
        foo()
    }
}