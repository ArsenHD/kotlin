class A {
    static {
        fun foo() = 42

        val x: Int = 123
    }
}

fun process(block: A.static.() -> Unit) {
    A.block()
}

fun box(): String {
    return process {
        if (foo() != 42 || x != 123) "fail" else "OK"
    }
}
