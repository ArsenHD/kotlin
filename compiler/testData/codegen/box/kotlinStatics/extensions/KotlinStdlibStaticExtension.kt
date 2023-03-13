// WITH_STDLIB

fun <T> List.static.empty(): List<T> = emptyList()

fun box() = if (List.empty<Int>().isNotEmpty()) "fail" else "OK"
