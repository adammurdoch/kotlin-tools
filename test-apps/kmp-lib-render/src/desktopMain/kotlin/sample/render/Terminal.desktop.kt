package sample.render

expect fun isAnsiTerminal(): Boolean

actual fun terminal(): Terminal {
    return if (isAnsiTerminal()) {
        AnsiTerminal
    } else {
        Terminal.Plain
    }
}

private data object AnsiTerminal : Terminal() {
    override fun literal(value: Any) {
        print("\u001B[31m")
        print(value)
        print("\u001B[39m")
    }

    override fun operator(value: Any) {
        print("\u001B[34m")
        print(value)
        print("\u001B[39m")
    }
}
