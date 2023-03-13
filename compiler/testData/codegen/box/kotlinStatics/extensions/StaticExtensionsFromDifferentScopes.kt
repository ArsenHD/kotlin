class A

fun A.static.foo() = 42

fun test1(): Int = A.foo()

class B {
    fun A.static.foo() = "abc"

    fun test2(): String = A.foo()
}

fun box(): String {
    if (test1() != 42) return "fail"
    if (B().test2() != "abc") return "fail"
    return "OK"
}
