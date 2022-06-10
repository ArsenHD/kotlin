class A {
    static {
        fun test1() {}
    }

    static {
        fun test2() {}
    }
}

fun foo() {
    A.test1()
    A.test2()
}
