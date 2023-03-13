class A {
    static {
        fun foo() = 42

        val x: Int = 123
    }
}

fun <T> process(block: A.static.() -> T): T = A.block()

fun box(): String {
    return process {
        if (foo() != 42 || x != 123) "fail" else "OK"
    }
}
