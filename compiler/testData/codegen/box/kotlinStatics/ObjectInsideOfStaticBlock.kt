class A {
    static {
        object B {
            val x = 42
        }
    }
}

fun box() = if (A.B.x != 42) "fail" else "OK"
