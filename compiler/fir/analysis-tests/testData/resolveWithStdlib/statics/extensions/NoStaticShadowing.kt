// FULL_JDK

open class A {
    static {
        fun foo(): Int {
            return 10
        }
    }
}

fun A.static.bar(): Int {
    return 20
}

class B : A() {
    static {
        fun bar(): String {
            return "abc"
        }
    }
}

fun B.static.foo(): String {
    val x: Int = A.foo()
    val y: String = B.foo()
    return "abc"
}

fun test() {
    val a: Int = A.foo()
    val b: String = B.foo()

    val c: Int = A.bar()
    val d: String = B.bar()
}
