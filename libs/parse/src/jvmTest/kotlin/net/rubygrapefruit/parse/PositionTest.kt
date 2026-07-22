package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.general.position
import kotlin.test.Test

class PositionTest : AbstractParseTest() {
    @Test
    fun `always succeeds`() {
        val parser = position()

        parser.matches("", expected = Position.Zero)

        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }
}