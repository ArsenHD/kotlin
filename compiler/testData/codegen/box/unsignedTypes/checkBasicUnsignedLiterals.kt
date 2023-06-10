// WITH_STDLIB
class A {
    static {
        val x = 42
    }

    companion object
}

fun box(): String {
//    val good = 42.toUInt()
//    val u1 = 1u
//    val u2 = 2u
//    val u3 = u1 + u2
//    if (u3.toInt() != 3) return "fail"
//
//    val max = 0u.dec().toLong()
    val expected2 = Int.MAX_VALUE * 2L + 1
//    val expected = A.x
//    if (max != expected) return "fail"

    return "OK"
}