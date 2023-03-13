class A {
    static {
        class B {
            fun foo() = 42
        }
    }
}

fun box() = if (A.B().foo() != 42) "fail" else "OK"
