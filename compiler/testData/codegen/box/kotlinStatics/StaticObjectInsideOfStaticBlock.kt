class A {
    static {
        static object B {
            fun foo() = 42
        }
    }
}

fun box() = if (A.B.foo() != 42) "fail" else "OK"
