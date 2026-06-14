package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.general.endOfInput
import kotlin.test.Test

class DescribedAsOfEndOfInputTest : AbstractParseTest() {
    @Test
    fun `replaces expectation`() {
        val parser = describedAs(endOfInput(), "<eoi>")

        parser.expecting {
            expectDescribed("<eoi>") {
                expectEndOfInput()
            }
        }

        parser.matches("")

        parser.doesNotMatch("X") {
            expect("<eoi>")
        }
    }
}