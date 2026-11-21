package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.fail

class OneStringOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = params(option("a")) { a -> a?.length }

        parser.edges {
            nonPositional {
                matches("-a", "one") {
                    endOfInput(3)
                }
            }
            endOfInput(null)
        }
    }

    @Test
    fun `cannot include other input values`() {
        val parser = params(option("a")) { _ -> fail() }

        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
        }
        parser.fails("value") {
            failure(0, "Unexpected argument 'value'")
        }

        parser.fails("-a", "one", "-unknown") {
            failure(2, "Unexpected option '-unknown'")
        }
        parser.fails("value", "-a", "one") {
            failure(0, "Unexpected argument 'value'")
        }
    }

    @Test
    fun `option must be well formed`() {
        val parser = params(option("a")) { a -> a?.length }

        parser.edges {
            nonPositional {
                doesNotMatch("--a", "value", count = 2, message = "Unexpected option '--a'. Try '-a' instead")
            }
            endOfInput(null)
        }
    }
}