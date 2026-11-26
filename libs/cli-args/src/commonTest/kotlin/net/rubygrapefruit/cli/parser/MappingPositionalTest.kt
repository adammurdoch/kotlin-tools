package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class MappingPositionalTest : AbstractParseTest() {
    @Test
    fun `can parse positional`() {
        val parser = positional("a", map("a thing") { StringType.Success(it.length) })

        parser.edges {
            positional {
                matches("12") {
                    endOfInput(2)
                }
                matches("") {
                    endOfInput(0)
                }
                expectingPositional("a")
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = positional("a", map("a thing") { StringType.Success(it.length) })

        parser.usage("<a>")
        parser.noOptions()
        parser.positional("<a>")
    }

    @Test
    fun `argument must be provided`() {
        val parser = positional("a", map("a thing") { StringType.Success(it.length) })

        parser.fails {
            expectedPositional(0, "a")
        }
    }

    @Test
    fun `argument must be well formed`() {
        val parser = positional("a", map("a thing") { StringType.Failure })

        parser.fails("broken") {
            failure(0, "Unexpected argument 'broken'. Expected a thing instead")
        }
    }

    @Test
    fun `cannot provide extra inputs`() {
        val parser = positional("a", map("a thing") { StringType.Success(it.length) })

        parser.fails("one", "unknown") {
            failure(1, "Unexpected argument 'unknown'")
        }
    }
}