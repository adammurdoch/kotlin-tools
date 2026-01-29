package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test
import kotlin.test.fail

class ChoiceOfSequence3Test : AbstractParseTest() {
    @Test
    fun `does not call map function of discarded option`() {
        val parser = oneOf(
            sequence(literal("ab", 1), literal("c", 2)) { a, b -> listOf(a, b) },
            sequence(literal("a", 5), literal("b", 6), literal("!", 7)) { _, _, _ -> fail() }
        )

        parser.matches("abc", expected = listOf(1, 2))
    }
}