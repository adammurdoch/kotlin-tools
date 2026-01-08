package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class ChoiceOfSequenceTest : AbstractParseTest() {
    @Test
    fun `matches choice of sequences`() {
        val parser = oneOf(
            sequence(literal("ab", 1), literal("c", 2)) { a, b -> a + b },
            sequence(literal("a", 5), literal("b", 1)) { a, b -> a + b }
        )

        parser.matches("abc", expected = 3)
        parser.matches("ab", expected = 6)
    }
}