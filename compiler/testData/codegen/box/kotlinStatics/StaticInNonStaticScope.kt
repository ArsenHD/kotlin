class A {
    fun foo() = bar()

    static {
        fun bar() = 42
    }
}

fun box() = if (A().foo() != 42) "fail" else "OK"
