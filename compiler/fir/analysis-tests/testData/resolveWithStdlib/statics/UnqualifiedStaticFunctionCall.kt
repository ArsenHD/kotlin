class A {
    static {
        fun foo() {}
    }

    fun test() {
        A.foo()
        foo()
    }
}
