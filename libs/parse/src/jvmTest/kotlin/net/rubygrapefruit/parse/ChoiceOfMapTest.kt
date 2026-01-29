package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test
import kotlin.test.fail

class ChoiceOfMapTest : AbstractParseTest() {
    @Test
    fun `does not call map function of discarded option`() {
        val parser = oneOf(
            map(literal("abc", 1)) { it.toString() },
            map(literal("ab", 2)) { fail() }
        )

        parser.matches("abc", expected = "1")
    }
}