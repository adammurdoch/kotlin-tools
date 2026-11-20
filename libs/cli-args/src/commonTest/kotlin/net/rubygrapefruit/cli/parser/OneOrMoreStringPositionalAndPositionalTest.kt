package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class OneOrMoreStringPositionalAndPositionalTest : AbstractParseTest() {
    @Test
    fun `repeated positional is reluctant`() {
        val parser = params(positional("a").oneOrMore(), positional("b")) { a, b -> Pair(a, b) }

        parser.edges {
            positional {
                matches("one") {
                    positional {
                        matches("two") {
                            positional { }
                            positional { }
                        }
                    }
                    positional {
                        matches("two") {
                            endOfInput(Pair(listOf("one"), "two"))
                        }
                        expectingPositional("b")
                    }
                }
                expectingPositional("a")
            }
        }
    }

    @Test
    fun `parameters must be provided`() {
        val parser = params(positional("a").oneOrMore(), positional("b")) { a, b -> Pair(a, b) }

        parser.fails {
            expectedParameter(0, "a")
        }
        parser.fails("one") {
            expectedParameter(1, "b")
        }
    }
}