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
// TODO: this is just an experiment, rollback these changes later
//class A {
//    fun foo() {}
//
//    static {
//        fun baz() {}
//    }
//}
//
//fun A.bar() {
//    foo()
//    baz()
//    A.baz()
//}