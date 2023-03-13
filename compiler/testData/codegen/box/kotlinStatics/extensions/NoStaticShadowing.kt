open class A {
    static {
        fun foo() = 10
    }
}

fun A.static.bar() = 20

class B : A() {
    static {
        fun bar() = "abc"
    }
}

fun B.static.foo(): String = "abc"

fun box(): String {
    if (A.foo() != 10) return "fail"
    if (B.foo() != "abc") return "fail"

    if (A.bar() != 20) return "fail"
    if (B.bar() != "abc") return "fail"

    return "OK"
}
