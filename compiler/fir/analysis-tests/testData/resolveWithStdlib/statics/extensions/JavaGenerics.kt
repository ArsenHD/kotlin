// FULL_JDK
import java.util.Optional

fun <T : Any> Optional.static.make(value: T): Optional<T> {
    return Optional.of(value)
}

fun test() {
    val opt1: Optional<Int> = Optional.make(1)
    val opt2: Optional<String> = Optional.make("abc")
}
