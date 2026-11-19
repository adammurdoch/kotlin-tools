package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.fail

class MultipleStringPositionalsTest : AbstractParseTest() {
    @Test
    fun `parses two positionals in order`() {
        val parser = params(positional("a"), positional("b")) { a, b -> listOf(a, b) }

        parser.edges {
            positional {
                matches("one") {
                    positional {
                        matches("two") {
                            endOfInput(listOf("one", "two"))
                        }
                    }
                    expectingPositional("b")
                }
            }
            expectingPositional("a")
            expectingPositional("b")
        }
    }

    @Test
    fun `parses three positionals in order`() {
        val parser = params(positional("a"), positional("b"), positional("c")) { a, b, c -> listOf(a, b, c) }

        parser.edges {
            positional {
                matches("one") {
                    positional {
                        matches("two") {
                            positional {
                                matches("three") {
                                    endOfInput(listOf("one", "two", "three"))
                                }
                            }
                            expectingPositional("c")
                        }
                    }
                    expectingPositional("b")
                    expectingPositional("c")
                }
            }
            expectingPositional("a")
            expectingPositional("b")
            expectingPositional("c")
        }
    }

    @Test
    fun `reports usage of three positionals`() {
        val parser = params(positional("a"), positional("b"), positional("c")) { _, _, _ -> fail() }

        parser.usage("<a> <b> <c>")
        parser.noOptions()
        parser.positionals {
            positional("<a>")
            positional("<b>")
            positional("<c>")
        }
    }

    @Test
    fun `positionals must be provided`() {
        val parser = params(positional("a"), positional("b")) { _, _ -> fail() }

        parser.fails {
            failure(0, "Parameter <a> not provided")
            failure(0, "Parameter <b> not provided")
        }
        parser.fails("one") {
            failure(1, "Parameter <b> not provided")
        }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Parameter <a> not provided")
            failure(1, "Parameter <b> not provided")
        }
        parser.fails("one", "--unknown") {
            failure(1, "Unexpected option '--unknown'")
            failure(2, "Parameter <b> not provided")
        }
    }

    @Test
    fun `cannot include other input values`() {
        val parser = params(positional("a"), positional("b")) { _, _ -> fail() }

        parser.fails("one", "two", "other") {
            failure(2, "Unexpected argument 'other'")
        }
        parser.fails("one", "--unknown", "two") {
            failure(1, "Unexpected option '--unknown'")
        }
        parser.fails("one", "-unknown", "two") {
            failure(1, "Unexpected option '-unknown'")
        }
    }
}