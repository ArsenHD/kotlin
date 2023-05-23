// WITH_STDLIB
import org.jetbrains.kotlin.fir.plugin.WithBuilder

@WithBuilder
data class MyClass(
    val number: Int,
    val list: List<Int>
    val set: Set<String>
    val map: Map<Int, String>
)

fun box(): String {
    val value = MyClass.build {
        number = 42

        list += 1
        list += listOf(2, 3)

        set += setOf("some", "text")

        map[1] = "one"
        map[2] = "two"
        map[3] = "three"
    }

    if (value.number != 42) return "Error"
    if (value.list != listOf(1, 2, 3)) return "Error"
    if (value.set != setOf("some", "text")) return "Error"
    if (value.map != mapOf(1 to "one", 2 to "two", 3 to "three")) return "Error"
    return "OK"
}
