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
            expectedParameter(0, "a")
        }
        parser.fails("100") {
            expectedParameter(1, "b")
        }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'. Expected parameter <a> instead")
        }
        parser.fails("12", "--unknown") {
            failure(1, "Unexpected option '--unknown'. Expected parameter <b> instead")
        }
    }

    @Test
    fun `positionals must be unsigned integers`() {
        val parser = params(
            positional("a", unsignedInteger()),
            positional("b", unsignedInteger())
        ) { _, _ -> fail() }

        parser.fails("one", "two") {
            failure(0, "'one' is not an unsigned integer")
            failure(1, "'two' is not an unsigned integer")
            failure(2, "Expected parameter <a>")
        }
        parser.fails("one", "two", "three") {
            failure(0, "'one' is not an unsigned integer")
            failure(1, "'two' is not an unsigned integer")
            failure(2, "'three' is not an unsigned integer")
            failure(3, "Expected parameter <a>")
        }
        parser.fails("one", "2") {
            failure(0, "'one' is not an unsigned integer")
            failure(2, "Expected parameter <b>")
        }
        parser.fails("1", "two") {
            failure(1, "'two' is not an unsigned integer")
            failure(2, "Expected parameter <b>")
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
            failure(0, "'unknown' is not an unsigned integer")
        }
        parser.fails("1", "unknown", "2", "unknown") {
            failure(1, "'unknown' is not an unsigned integer")
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