package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.not
import net.rubygrapefruit.parse.general.endOfInput
import kotlin.test.Test

class NotOfEndOfInputTest : AbstractParseTest() {
    @Test
    fun `does not match anything`() {
        val parser = not(endOfInput())

        parser.expecting {
            expectNot {
                expectEndOfInput()
            }
        }

        parser.doesNotMatch("") {
            expect("end of input")
            expect("not end of input")
        }
        parser.doesNotMatch("X") {
            expect("end of input")
            expect("not end of input")
        }
    }
}