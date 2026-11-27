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
    override fun identifier(value: Any) {
        print("\u001B[35m") // magenta
        print(value)
        print("\u001B[39m") // default foreground
    }

    override fun literal(value: Any) {
        print("\u001B[32m") // green
        print(value)
        print("\u001B[39m") // default foreground
    }

    override fun operator(value: Any) {
        print("\u001B[34m") // blue
        print(value)
        print("\u001B[39m") // default foreground
    }
}
