class A {
    static {
        fun foo() = 42
    }

    static {
        fun bar() = "abc"
    }
}

fun box(): String {
    if (A.foo() != 42) return "fail"
    if (A.bar() != "abc") return "fail"
    return "OK"
}
