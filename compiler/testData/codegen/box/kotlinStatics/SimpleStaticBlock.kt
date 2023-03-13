class A {
    fun foo() = 42

    static {
        fun bar() = 123

        val x: Int = 456
    }
}

fun box(): String {
    if (A().foo() != 42) return "fail"
    if (A.bar() != 123) return "fail"
    if (A.x != 456) return "fail"
    return "OK"
}
