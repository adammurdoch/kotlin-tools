package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class OneOrMoreStringPositionalAndPositionalTest : AbstractParseTest() {
    @Test
    fun `repeated positional is greedy`() {
        val parser = params(positional("a").oneOrMore(), positional("b")) { a, b -> a + b }

        parser.fails("one") {
            expectedParameter(1, "b")
        }
        parser.fails("one", "two") {
            expectedParameter(2, "b")
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