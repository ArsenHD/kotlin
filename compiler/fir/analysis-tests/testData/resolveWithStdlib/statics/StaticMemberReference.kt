class A {
    static {
        val value = 123
        fun foo() = 42
        fun bar(x: Int, y: Int) = x + y
    }
}

fun test1(property: KProperty<Int>) {}
fun test2(block: () -> Int) = block()
fun test3(block: (Int, Int) -> Int) = block(2, 3)

fun test3() {
    test1(A::value)
    val x: Int = test2(A::foo)
    val y: Int = test3(A::bar)
}
