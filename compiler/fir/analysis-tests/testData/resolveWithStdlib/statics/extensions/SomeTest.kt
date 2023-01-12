// FULL_JDK

open class A {
    static {
        fun foo(): Int {
            return 10
        }
    }
}

fun A.static.bar() {
}

class B : A() {
    static {
        fun bar() {
        }
    }
}

fun B.static.foo(): String {
    A.foo()
    B.foo()
    return "abc"
}

fun test() {
    A.foo()
    B.foo()

    A.bar() // ext?
    B.bar() // member?

    // can't access static extension, because method `A::foo` shadows it
//    val x: Int = A.foo()
}
