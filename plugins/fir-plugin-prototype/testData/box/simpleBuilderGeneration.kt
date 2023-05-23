// WITH_STDLIB
import org.jetbrains.kotlin.fir.plugin.WithBuilder

@WithBuilder
data class MyClass(val number: Int, val text: String, val flag: Boolean)

fun box(): String {
    val value = MyClass.build {
        number = 42
        text = "text"
        flag = true
    }
    return if (value.number == 42 && value.text == "text" && value.flag) "OK" else "Error"
}
