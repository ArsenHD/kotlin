class A {
    static {
        fun foo() {}

        val x: Int = 0
    }
}

fun process(block: A.static.() -> Unit) {
    A.block()
}

fun test() {
    process {
        foo()
        val y = x
    }
}
