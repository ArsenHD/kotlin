// WITH_STDLIB
// WITH_COROUTINES

import helpers.*
import kotlin.coroutines.*

fun box(): String {
    val x = EmptyContinuation
    return "OK"
}