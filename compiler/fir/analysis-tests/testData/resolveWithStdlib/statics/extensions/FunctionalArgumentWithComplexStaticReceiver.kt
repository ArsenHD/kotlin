class MyClass {
    class A {
        class B
    }
}

fun test1(block: MyClass.A.B.static.() -> Unit) {
    MyClass.A.B.block()
}
