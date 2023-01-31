class A {
    fun foo() {}

    static {
        fun baz() {}
    }
}

fun A.bar() {
    foo()
    <!UNRESOLVED_REFERENCE!>baz<!>()
    A.baz()
}
