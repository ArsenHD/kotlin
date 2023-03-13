class A {
    static {
        fun foo() = 42
    }
}

fun <T> process(block: A.static.() -> T): T = A.block()

fun box() = process { if (foo() != 42) "fail" else "OK" }

