class A {
    static {
        fun <T> foo(x: T): T = x
    }
}

fun <T> A.static.bar(y: T): T = y

fun box(): String {
    if (A.foo(1) != 1) return "fail"
    if (A.foo("abc") != "abc") return "fail"

    if (A.bar(1) != 1) return "fail"
    if (A.bar("abc") != "abc") return "fail"

    return "OK"
}
