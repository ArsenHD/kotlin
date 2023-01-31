// FULL_JDK

class A {
    static {
        fun <T> foo(x: T): T {
            return x
        }
    }
}

fun <T> A.static.bar(y: T): T {
    return y
}

fun test() {
    val a: Int = A.foo(1)
    val b: String = A.foo("abc")

    val x: Int = A.bar(1)
    val y: String = A.bar("abc")
}
