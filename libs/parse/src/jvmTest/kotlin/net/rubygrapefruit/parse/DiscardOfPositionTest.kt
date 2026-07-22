package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.general.position
import kotlin.test.Test

class DiscardOfPositionTest : AbstractParseTest() {
    @Test
    fun `discards result of position`() {
        val parser = discard(position())

        parser.expecting {
            expectSucceed(result = Unit)
        }

        parser.matches("")

        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }
}