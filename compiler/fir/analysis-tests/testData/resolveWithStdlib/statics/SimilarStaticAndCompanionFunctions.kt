class A {
    static {
        fun foo(a: Int): Int {
            return 0
        }
    }

    companion object {
        fun foo(a: Int): String {
            return ""
        }
    }
}

fun test() {
    val x: Int = A.foo(12)
}
