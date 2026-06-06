package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class CharLiteralTest : AbstractParseTest() {
    @Test
    fun `matches single char literal`() {
        val parser = literal("a")

        parser.expecting {
            expectLiteral("a")
        }

        parser.matches("a") {
            steps {
                advance(1)
            }
        }

        // missing
        parser.doesNotMatch("") {
            // don't use expectLiteral() here, to check formatting
            expect("\"a\"")
            steps {}
        }

        // unexpected char
        parser.doesNotMatch("X") {
            expect("\"a\"")
            steps {}
        }

        // extra char
        parser.doesNotMatch("aX") {
            failAt(1)
            expectEndOfInput()
            steps {
                advance(1)
            }
        }
        parser.doesNotMatch("aXXX") {
            failAt(1)
            expectEndOfInput()
            steps {
                advance(1)
            }
        }

        // incorrect case
        parser.doesNotMatch("A") {
            expect("\"a\"")
            steps {}
        }
    }

    @Test
    fun `matches single new line character`() {
        val parser = literal("\n")

        parser.expecting {
            expectLiteral("\n")
        }

        parser.matches("\n")

        // missing
        parser.doesNotMatch("") {
            // don't use expectLiteral() here, to check formatting
            expect("new line")
        }

        // unexpected char
        parser.doesNotMatch("X") {
            expect("new line")
        }

        // extra char
        parser.doesNotMatch("\nX") {
            failAt(1, 2, 1)
            expectContext("", "X")
            expectEndOfInput()
        }
    }

    @Test
    fun `formats special chars`() {
        val candidates = listOf(
            "\t" to "tab",
            "\n" to "new line",
            "\r" to "carriage return",
            "\r\n" to "carriage return new line",
            " " to "space",
            "\"" to "'\"'"
        )
        for (candidate in candidates) {
            val parser = literal(candidate.first)

            parser.matches(candidate.first)

            // missing
            parser.doesNotMatch("") {
                expect(candidate.second)
            }
        }
    }

    @Test
    fun `matches single char literal and produces result`() {
        val parser = literal("a", 1)

        parser.expecting {
            expectLiteral("a", result = 1)
        }

        parser.matches("a", expected = 1)
    }

    @Test
    fun `matches multi-char literal`() {
        val parser = literal("ab")

        parser.expecting {
            expectLiteral("ab")
        }

        parser.matches("ab") {
            steps {
                advance(2)
            }
        }

        // missing
        parser.doesNotMatch("") {
            // don't use expectLiteral() here, to check formatting
            expect("\"ab\"")
            steps {}
        }
        parser.doesNotMatch("a") {
            expect("\"ab\"")
            steps {}
        }
        parser.doesNotMatch("b") {
            expect("\"ab\"")
            steps {}
        }

        // unexpected char
        parser.doesNotMatch("X") {
            expect("\"ab\"")
            steps {}
        }
        parser.doesNotMatch("aX") {
            expect("\"ab\"")
            steps {}
        }
        parser.doesNotMatch("Xb") {
            expect("\"ab\"")
            steps {}
        }
        parser.doesNotMatch("Xab") {
            expect("\"ab\"")
            steps {}
        }
        parser.doesNotMatch("aXb") {
            expect("\"ab\"")
            steps {}
        }

        // extra char
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
            steps {
                advance(2)
            }
        }
        parser.doesNotMatch("abXX") {
            failAt(2)
            expectEndOfInput()
            steps {
                advance(2)
            }
        }

        // incorrect case
        parser.doesNotMatch("AB") {
            expect("\"ab\"")
            steps {}
        }
        parser.doesNotMatch("Ab") {
            expect("\"ab\"")
            steps {}
        }
        parser.doesNotMatch("aB") {
            expect("\"ab\"")
            steps {}
        }
    }

    @Test
    fun `matches multi-char literal and produces result`() {
        val parser = literal("ab", 1)

        parser.expecting {
            expectLiteral("ab", result = 1)
        }

        parser.matches("ab", expected = 1)
    }
}