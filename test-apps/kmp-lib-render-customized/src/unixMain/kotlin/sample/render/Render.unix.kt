package sample.render

import platform.posix.isatty

actual fun terminal(): Terminal {
    return if (isatty(1) != 0) {
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
