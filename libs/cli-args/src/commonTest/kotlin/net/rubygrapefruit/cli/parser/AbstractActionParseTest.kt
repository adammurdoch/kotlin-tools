package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output

abstract class AbstractActionParseTest : AbstractParseTest() {
    protected object TestContext : Runnable.Context {
        override val output: Output
            get() = TODO("Not yet implemented")
        override val diagnostics: Output
            get() = TODO("Not yet implemented")
    }
}