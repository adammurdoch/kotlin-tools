package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.fail

class OneOrMoreStringPositionalAndPositionalTest : AbstractParseTest() {
    @Test
    fun `repeated positional is greedy`() {
        val parser = params(positional("a").oneOrMore(), positional("b")) { _, _ -> fail() }

        parser.fails("one") {
            failure(1, "Parameter <b> not provided")
        }
        parser.fails("one", "two") {
            failure(2, "Parameter <b> not provided")
        }

        parser.edges {
            positional {
                matches("one") {
                    positional {  }
                    positional {  } // this will never be matched, should probably fail early
                    expectingPositional("b")
                }
            }
            expectingPositional("a")
        }
    }
}