class A {
    static {
        fun foo() {}
        val x: Int = 0
    }
}

fun A.static.bar() {}

fun A.static.baz() {
    foo()
    bar()
    val y = x
}
