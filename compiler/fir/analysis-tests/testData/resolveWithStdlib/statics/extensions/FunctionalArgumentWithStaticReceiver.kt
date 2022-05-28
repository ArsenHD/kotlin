class A

fun test1(block: A.static.() -> Unit) {
    A.block()
}
