package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class ZeroOrMoreStringPositionalAndPositionalTest : AbstractParseTest() {
    @Test
    fun `repeated positional is reluctant`() {
        val parser = params(positional("a").zeroOrMore(), positional("b")) { a, b -> Pair(a, b) }

        parser.edges {
            positional {
                matches("one") {
                    positional { }
                    positional { }
                    expectingPositional("b")
                }
            }
            positional {
                matches("one") {
                    endOfInput(listOf("one"))
                }
            }
            expectingPositional("b")
        }
    }

    @Test
    fun `parameter must be provided`() {
        val parser = params(positional("a").zeroOrMore(), positional("b")) { a, b -> Pair(a, b) }

        parser.fails {
            expectedParameter(0, "b")
        }
    }
}