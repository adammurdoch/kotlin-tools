package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import java.io.ByteArrayInputStream
import kotlin.test.Test

class InputStreamParseTest : AbstractParseTest() {
    @Test
    fun `parses the contents of an input stream`() {
        val inputStream = ByteArrayInputStream(byteArrayOf(0x1, 0x2, 0x3))

        val parser = map(zeroOrMore(oneInRange(0x1, 0x10))) { it.size }

        val result = parser.parse(inputStream)
        result.assertIsSuccess(3)
    }
}