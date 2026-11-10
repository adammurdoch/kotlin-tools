package net.rubygrapefruit.cli.terminal

private object DefaultOutput : AbstractPlainOutput() {
    override fun put(text: CharSequence) {
        print(text)
    }
}

private object DefaultDiagnostics : AbstractPlainOutput() {
    override fun put(text: CharSequence) {
        System.err.print(text)
    }
}

actual fun output(): Output = DefaultOutput

actual fun diagnostics(): Output = DefaultDiagnostics
