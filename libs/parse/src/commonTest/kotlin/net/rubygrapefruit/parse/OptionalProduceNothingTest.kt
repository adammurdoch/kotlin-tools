package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.optional
import kotlin.test.Test

class OptionalProduceNothingTest : AbstractParseTest() {
    @Test
    fun `can match optional literal`() {
        val parser = optional(literal(byteArrayOf(0x1, 0x2)))

        parser.expecting {
            expectChoice {
                expectLiteral(0x1, 0x2)
                expectSucceed()
            }
        }

        parser.matches(0x1, 0x2)
        parser.matches()
    }
}