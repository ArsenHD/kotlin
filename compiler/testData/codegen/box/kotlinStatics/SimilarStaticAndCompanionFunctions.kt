class A {
    static {
        fun foo(a: Int): Int = a
    }

    companion object {
        fun foo(a: Int): String = "abc"
    }
}

fun box() = if (A.foo(42) != 42) "fail" else "OK"
