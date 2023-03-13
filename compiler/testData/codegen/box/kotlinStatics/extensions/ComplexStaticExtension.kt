class MyClass {
    class MyNestedClass {
        class AnotherNestedClass
    }
}

fun MyClass.MyNestedClass.AnotherNestedClass.static.test1(): Int {
    return 42
}

fun box() = if (MyClass.MyNestedClass.AnotherNestedClass.test1() == 42) "OK" else "fail"
