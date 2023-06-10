import kotlin.reflect.KProperty0

class A

val A.static.value
    get() = 123

fun A.static.foo() = 42
fun A.static.bar(x: Int, y: Int) = x + y

fun test1(property: KProperty0<Int>) {}
fun test2(block: () -> Int) = block()
fun test3(block: (Int, Int) -> Int) = block(2, 3)

fun test4() {
    test1(A::value)
    val x: Int = test2(A::foo)
    val y: Int = test3(A::bar)
}
