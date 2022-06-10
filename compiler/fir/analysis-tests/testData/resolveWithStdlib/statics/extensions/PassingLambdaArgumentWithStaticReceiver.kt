class A

fun A.static.foo() {}

fun bar(block: A.static.() -> Unit) {}

fun test() {
    bar {
        foo()
    }
}
