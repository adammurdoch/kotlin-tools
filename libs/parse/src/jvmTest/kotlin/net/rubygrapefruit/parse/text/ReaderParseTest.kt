package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import java.io.StringReader
import kotlin.test.Test

class ReaderParseTest : AbstractParseTest() {
    @Test
    fun `parses the contents of a reader`() {
        val reader = StringReader("abc")

        val parser = map(zeroOrMore(oneInRange('a'..'z'))) { it.size }

        val result = parser.parse(reader)
        result.assertIsSuccess(3)
    }
}