class A {
    static {
        fun foo() {}
        // TODO: fix error
//        val x: Int = 0
    }
}

fun A.static.bar() {}

fun A.static.baz() {
    foo()
    bar()
//    val y = x
}
