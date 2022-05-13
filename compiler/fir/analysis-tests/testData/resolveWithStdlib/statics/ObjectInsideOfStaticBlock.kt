class A {
    static {
        object B {
            val x: Int = 10
            val y: Int = 20
        }
    }
}

fun foo() {
    val x = A.B.x
    val y = A.B.y
}
