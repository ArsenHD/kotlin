class A {
    fun A.static.foo() = 42

    fun bar() = A.foo()
}

fun box() = if (A().bar() != 42) "fail" else "OK"