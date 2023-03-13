class A {
    class B {
        class C {
            static {
                fun foo() = 42
            }
        }
    }
}

fun <T> process(block: A.B.C.static.() -> T): T = A.B.C.block()

fun box(): String = process {
    if (foo() != 42) "fail" else "OK"
}
