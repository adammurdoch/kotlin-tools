package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class RepeatWithSeparatorTest : AbstractParseTest() {
    @Test
    fun `matches n char literals with separator`() {
        val parser = repeat(3, literal("ab", 1), literal(",", 2))

        parser.matches("ab,ab,ab", expected = listOf(1, 1, 1))
    }
}