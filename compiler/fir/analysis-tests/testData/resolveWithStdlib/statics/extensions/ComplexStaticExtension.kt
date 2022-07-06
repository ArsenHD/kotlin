class MyClass {
    class MyNestedClass {
        class AnotherNestedClass
    }
}

fun MyClass.MyNestedClass.AnotherNestedClass.static.test1() {
}

fun foo() {
    MyClass.MyNestedClass.AnotherNestedClass.test1()
}
