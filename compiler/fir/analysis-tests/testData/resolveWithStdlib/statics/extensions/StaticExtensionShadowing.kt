// FULL_JDK

class A {
    static {
        fun foo(): Int {
            return 10
        }
    }
}

fun A.static.foo(): String {
    return "abc"
}

fun test() {
    // static extension is shadowed by a regular static method
    val x: Int = A.foo()
}
