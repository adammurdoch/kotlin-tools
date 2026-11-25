package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.fail

class MultipleUnsignedIntPositionalsTest : AbstractParseTest() {
    @Test
    fun `parses two positionals in order`() {
        val parser = params(
            positional("a", unsignedInteger()),
            positional("b", unsignedInteger())
        ) { a, b -> listOf(a, b) }

        parser.edges {
            positional {
                matches("1") {
                    positional {
                        matches("2") {
                            endOfInput(listOf(1, 2))
                        }
                        expectingPositional("b")
                    }
                }
                expectingPositional("a")
            }
        }
    }

    @Test
    fun `positionals must be provided`() {
        val parser = params(
            positional("a", unsignedInteger()),
            positional("b", unsignedInteger())
        ) { _, _ -> fail() }

        parser.fails {
            expectedPositional(0, "a")
        }
        parser.fails("100") {
            expectedPositional(1, "b")
        }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'. Expected an unsigned int instead")
        }
        parser.fails("12", "--unknown") {
            failure(1, "Unexpected option '--unknown'. Expected an unsigned int instead")
        }
    }

    @Test
    fun `positionals must be unsigned integers`() {
        val parser = params(
            positional("a", unsignedInteger()),
            positional("b", unsignedInteger())
        ) { _, _ -> fail() }

        parser.fails("one", "two") {
            failure(0, "Unexpected argument 'one'. Expected an unsigned int instead")
            failure(1, "Unexpected argument 'two'. Expected an unsigned int instead")
        }
        parser.fails("one", "two", "three") {
            failure(0, "Unexpected argument 'one'. Expected an unsigned int instead")
            failure(1, "Unexpected argument 'two'. Expected an unsigned int instead")
            failure(2, "Unexpected argument 'three'. Expected an unsigned int instead")
        }
        parser.fails("one", "2") {
            failure(0, "Unexpected argument 'one'. Expected an unsigned int instead")
            failure(2, "Expected parameter <b>")
        }
        parser.fails("1", "two") {
            failure(1, "Unexpected argument 'two'. Expected an unsigned int instead")
        }
    }

    @Test
    fun `cannot provide extra inputs`() {
        val parser = params(
            positional("a", unsignedInteger()),
            positional("b", unsignedInteger())
        ) { _, _ -> fail() }

        parser.fails("1", "2", "unknown") {
            failure(2, "Unexpected argument 'unknown'")
        }
        parser.fails("unknown", "1", "2") {
            failure(0, "Unexpected argument 'unknown'")
        }
        parser.fails("1", "unknown", "2", "unknown") {
            failure(1, "Unexpected argument 'unknown'")
            failure(3, "Unexpected argument 'unknown'")
        }

        parser.fails("1", "2", "-unknown") {
            failure(2, "Unexpected option '-unknown'")
        }
        parser.fails("-unknown", "1", "2") {
            failure(0, "Unexpected option '-unknown'")
        }
    }
}