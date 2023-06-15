class Outer {
    inner class Inner
}

// TODO: 'Function' here now refers to Function.SSO, but such syntax is not allowed.
// TODO: There needs to be a checker to forbid SSO-expressions
val x = Outer.<!NO_COMPANION_OBJECT!>Inner<!>
val klass = Outer.Inner::class
