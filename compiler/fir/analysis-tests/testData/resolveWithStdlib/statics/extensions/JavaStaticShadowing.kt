// FULL_JDK
import java.util.OptionalInt

fun OptionalInt.static.of(value: Int): Int {
    return value
}

fun test() {
    // can't access static extension, because method `OptionalInt::of` shadows it
    val x: OptionalInt = OptionalInt.of(10)
}
