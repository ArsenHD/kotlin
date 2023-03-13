class A {
    static {
        fun foo() = 42
        fun bar(x: Int, y: Int) = x + y
    }
}

fun test1(block: () -> Int) = block()
fun test2(block: (Int, Int) -> Int) = block(2, 3)

fun box(): String {
    if (test1(A::foo) != 42) return "fail"
    if (test2(A::bar) != 5) return "fail"
    return "OK"
}
