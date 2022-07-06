static object MyObject {
    fun foo() {
        print("foo")
    }

    val x: Int = 10
}

fun bar() {
    MyObject.foo()
    val x = MyObject.x
}
