class A {
    static {
        fun foo() = 42

        val x = 123
    }
}

fun A.static.bar() = "abc"

fun A.static.baz(): String {
    if (foo() != 42) return "fail"
    if (bar() != "abc") return "fail"
    if (x != 123) return "fail"
    return "OK"
}

fun box(): String = A.baz()
