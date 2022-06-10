class A

fun A.static.foo() {}

val A.static.x: Int
    get() = 0

fun process(block: A.static.() -> Unit) {
    A.block()
}

fun test() {
    process {
        foo()
        val y = x
    }
}
