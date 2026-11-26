package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class MappingOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x", map("a thing") { StringType.Success(it.length) })

        parser.edges {
            nonPositional {
                matches("-x", "123") {
                    endOfInput(3)
                }
                matches("-x", "") {
                    endOfInput(0)
                }
                doesNotMatch("unknown")
                doesNotMatch("--other")
            }
            endOfInput(null)
        }
    }

}