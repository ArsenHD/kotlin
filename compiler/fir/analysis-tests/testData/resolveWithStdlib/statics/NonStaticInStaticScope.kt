class A {
    fun foo() {
        println("foo")
    }

    static {
        fun bar() {
            <!UNRESOLVED_REFERENCE!>foo<!>()
        }
    }
}
