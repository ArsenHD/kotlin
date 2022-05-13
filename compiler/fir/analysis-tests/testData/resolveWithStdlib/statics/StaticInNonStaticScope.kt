class A {
    fun foo() {
        bar()
    }

    static {
        fun bar() {
            println("bar")
        }
    }
}
