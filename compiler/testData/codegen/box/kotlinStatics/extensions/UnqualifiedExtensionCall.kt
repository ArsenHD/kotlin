class A {
    fun test() = foo()
}

fun A.static.foo() = 42

fun box() = if (A().test() != 42) "fail" else "OK"
