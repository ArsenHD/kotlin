class A

class B {
    fun A.static.foo() = 42

    fun bar() = A.foo()
}

fun box() = if (B().bar() != 42) "fail" else "OK"
