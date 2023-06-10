class A {
    static {
        fun foo() {}

        val x: Int = 0
    }
}
class B {
    static {
        fun bar() {}
        fun baz() {}
    }
}

fun <T> process(block: T.static.() -> Unit) {
    T.block()
}

fun test() {
    process<A> {
        foo()
        val y = x
    }
    process<B> {
        bar()
        baz()
    }
}
