package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.fail

class ZeroOrMoreStringPositionalAndPositionalTest : AbstractParseTest() {
    @Test
    fun `repeated positional is reluctant`() {
        val parser = params(positional("a").zeroOrMore(), positional("b")) { a, b -> Pair(a, b) }

        parser.edges {
            positional {
                matches("one") {
                    positional { }
                    positional {
                        matches("two") {
                            endOfInput(Pair(listOf("one"), "two"))
                        }
                        expectingPositional("b")
                    }
                }
            }
            positional {
                matches("one") {
                    endOfInput(Pair(emptyList<String>(), "one"))
                }
                expectingPositional("b")
            }
        }
    }

    @Test
    fun `parameter must be provided`() {
        val parser = params(positional("a").zeroOrMore(), positional("b")) { _, _ -> fail() }

        parser.fails {
            expectedPositional(0, "b")
        }
    }

    @Test
    fun `cannot provide extra input`() {
        val parser = params(positional("a").zeroOrMore(), positional("b")) { _, _ -> fail() }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'. Expected parameter <b> instead")
        }
        parser.fails("--unknown", "b") {
            failure(0, "Unexpected option '--unknown'")
        }
        parser.fails("b", "--unknown") {
            failure(1, "Unexpected option '--unknown'")
        }
        parser.fails("a", "--unknown", "b") {
            failure(1, "Unexpected option '--unknown'")
        }
    }
}