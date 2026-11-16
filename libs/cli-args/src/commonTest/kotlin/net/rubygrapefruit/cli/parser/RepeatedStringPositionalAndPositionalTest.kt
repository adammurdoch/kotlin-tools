package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.fail

class RepeatedStringPositionalAndPositionalTest : AbstractParseTest() {
    @Test
    fun `repeated positional is greedy`() {
        val parser = params(positional("a").repeated(), positional("b")) { _, _ -> fail() }

        parser.fails("one") {
            failure(1, "Parameter <b> not provided")
        }
        parser.fails("one", "two") {
            failure(2, "Parameter <b> not provided")
        }
    }
}