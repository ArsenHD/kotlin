static object MyObject {
    fun foo() {
        print("foo")
    }

    // TODO: fix error
    val x: Int = 10
}

fun bar() {
    MyObject.foo()
    val x = MyObject.x
}
