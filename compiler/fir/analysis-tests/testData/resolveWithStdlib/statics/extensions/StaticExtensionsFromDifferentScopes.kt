class A

fun A.static.foo(): Int {
    return 0
}

fun test1() {
    val x: Int = A.foo()
}

class B {
    fun A.static.foo(): String {
        return ""
    }

    fun test2() {
        val x: String = A.foo()
    }
}
