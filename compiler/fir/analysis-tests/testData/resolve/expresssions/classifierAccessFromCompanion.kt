class Factory {
    sealed class Function {
        object Default
    }

    companion object {
        // TODO: 'Function' here now refers to Function.SSO, but such syntax is not allowed.
        // TODO: There needs to be a checker to forbid SSO-expressions
        val f = <!NO_COMPANION_OBJECT!>Function<!>
        val x = Function.Default
    }
}
