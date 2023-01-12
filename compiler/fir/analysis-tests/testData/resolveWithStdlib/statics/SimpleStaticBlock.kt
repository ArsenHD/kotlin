class A {
    fun foo() {
        println("foo")
    }

    static {
        fun bar() {
            println("bar")
        }

        // TODO: fix error
//        val x: Int = 10
    }
}

fun baz() {
    val a = A()
    a.foo()
    A.bar()
    a.<!UNRESOLVED_REFERENCE!>bar<!>()
}
