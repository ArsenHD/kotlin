static object A {
    fun foo() = 42

    val x = 123
}

fun box(): String {
    if (A.foo() != 42) return "fail"
    if (A.x != 123) return "fail"
    return "OK"
}
