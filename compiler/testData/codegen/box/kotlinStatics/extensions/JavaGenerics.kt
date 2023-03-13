// FULL_JDK

import java.util.Optional

fun <T : Any> Optional.static.make(value: T): Optional<T> {
    return Optional.of(value)
}

fun box(): String {
    val opt1: Optional<Int> = Optional.make(1)
    if (opt1.get() != 1) return "fail"

    val opt2: Optional<String> = Optional.make("abc")
    if (opt2.get() != "abc") return "fail"

    return "OK"
}
