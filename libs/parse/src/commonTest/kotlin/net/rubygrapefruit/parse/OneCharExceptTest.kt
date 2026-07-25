package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.oneExcept
import kotlin.test.Test

class OneCharExceptTest : AbstractParseTest() {
    @Test
    fun `matches any char except single char literal`() {
        val parser = oneExcept(literal("!"))

        parser.expecting {
            expectSequence {
                expectNot {
                    expectLiteral("!")
                }
                expectOneChar()
            }
        }

        parser.matches("a", expected = 'a')

        // missing
        parser.doesNotMatch("") {
            expect("any character")
            expect("not \"!\"")
        }

        // matches predicate
        parser.doesNotMatch("!") {
            expect("any character")
            expect("not \"!\"")
        }

        // extra
        parser.doesNotMatch("aX") {
            failAt(1)
            expectEndOfInput()
        }
    }
}