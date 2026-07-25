package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AbstractParseTest
import kotlin.test.Test

class BinaryIntTest : AbstractParseTest() {
    @Test
    fun `parses 16 bit unsigned little endian int`() {
        val parser = uint16LittleEndian()

        parser.matches(0, 0, expected = 0.toUShort())
        parser.matches(0x1, 0, expected = 0x1.toUShort())
        parser.matches(0, 0x1, expected = 0x100.toUShort())
        parser.matches(0xAA.toByte(), 0xBB.toByte(), expected = 0xBBAA.toUShort())
        parser.matches(0xFF.toByte(), 0xFF.toByte(), expected = 0xFFFF.toUShort())

        // missing
        parser.doesNotMatch {
            expect("any byte")
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expect("any byte")
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `parses 16 bit unsigned big endian int`() {
        val parser = uint16BigEndian()

        parser.matches(0, 0, expected = 0.toUShort())
        parser.matches(0, 0x1, expected = 0x1.toUShort())
        parser.matches(0x1, 0, expected = 0x100.toUShort())
        parser.matches(0xAA.toByte(), 0xBB.toByte(), expected = 0xAABB.toUShort())
        parser.matches(0xFF.toByte(), 0xFF.toByte(), expected = 0xFFFF.toUShort())

        // missing
        parser.doesNotMatch {
            expect("any byte")
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expect("any byte")
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(2)
            expectEndOfInput()
        }
    }
}