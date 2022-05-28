// FULL_JDK
import java.io.BufferedReader

fun foo() {
    BufferedReader.myFun()
    val y = BufferedReader.x
}


fun BufferedReader.static.myFun() {
}

val BufferedReader.static.x: Int
    get() = 10
