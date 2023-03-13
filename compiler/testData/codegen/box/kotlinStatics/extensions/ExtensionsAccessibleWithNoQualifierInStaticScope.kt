class A

fun A.static.foo() = 42

val A.static.x: Int
    get() = 123

fun <T> process(block: A.static.() -> T): T = A.block()

fun box(): String = process {
    if (foo() != 42 || x != 123) "fail" else "OK"
}
