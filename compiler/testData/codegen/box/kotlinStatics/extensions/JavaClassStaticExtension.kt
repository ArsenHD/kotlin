import java.io.BufferedReader

fun BufferedReader.static.foo() = 42

val BufferedReader.static.x: Int
    get() = 123

fun box(): String {
    if (BufferedReader.foo() != 42) return "fail"
    if (BufferedReader.x != 123) return "fail"
    return "OK"
}
