package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.general.position
import kotlin.test.Test

class PositionTest : AbstractParseTest() {
    @Test
    fun `always succeeds`() {
        val parser = position()

        parser.expecting {
            expectPosition()
        }

        parser.matches("", expected = Position.Zero)

        // extra
        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }
}