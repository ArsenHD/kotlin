// FILE: A.kt
class A {
    static {
        fun foo() {}
    }
}

// FILE: B.kt
import A.foo

fun test() {
    foo()
}
