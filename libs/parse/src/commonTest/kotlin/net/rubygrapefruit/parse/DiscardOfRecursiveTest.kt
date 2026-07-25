package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.BinaryInput
import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.recursive
import kotlin.test.Test

class DiscardOfRecursiveTest : AbstractParseTest() {
    @Test
    fun `discards result of recursive parser`() {
        val recursive = recursive<BinaryInput, Int>()
        recursive.parser(literal(byteArrayOf(0x1), 1))

        val parser = discard(recursive)

        parser.expecting {
            expectRecursive {
                expectLiteral(0x1, result = Unit)
            }
        }

        parser.matches(0x1)

        parser.doesNotMatch() {
            expectLiteral(0x1)
        }
    }
}