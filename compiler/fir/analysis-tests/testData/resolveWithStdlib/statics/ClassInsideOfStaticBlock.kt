class A {
    static {
        class B {
            fun foo() {
                println("foo")
            }
        }
    }
}

fun bar() {
    val b = A.B()
    b.foo()
}