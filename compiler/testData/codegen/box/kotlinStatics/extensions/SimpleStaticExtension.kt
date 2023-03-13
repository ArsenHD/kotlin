class A

fun A.static.foo() = 42

fun box() = if (A.foo() != 42) "fail" else "OK"
