// FULL_JDK

class A {
    static {
        fun <T> foo(x: T) {}
    }
}

// TODO: add return type
fun <T> A.static.bar(y: T) {}

fun test() {
    A.foo(1)
    A.foo("abc")

    A.bar(1)
    A.bar("abc")
}
