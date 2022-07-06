class A {
    static {
        static object B {
            fun foo() {
                println("foo")
            }
        }
    }
}

fun bar() {
    A.B.foo()
}
