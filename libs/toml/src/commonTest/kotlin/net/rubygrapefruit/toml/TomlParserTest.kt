package net.rubygrapefruit.toml

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TomlParserTest {
    @Test
    fun `parses empty input`() {
        val parser = TomlParser()
        val table = parser.parse("")
        assertTrue(table.values.isEmpty())
    }

    @Test
    fun `parses whitespace only input`() {
        val parser = TomlParser()
        val table = parser.parse(
            """
# comment
# comment with #comment
        """.trimIndent()
        )
        assertTrue(table.values.isEmpty())
    }

    @Test
    fun `parses key and string value pair`() {
        val parser = TomlParser()
        val table = parser.parse("""key="value"""")
        assertEquals(1, table.values.size)
        table.values.first().isString("key", "value")
    }

    @Test
    fun `parses key and number value pair`() {
        val parser = TomlParser()
        val table = parser.parse("""key=12""")
        assertEquals(1, table.values.size)
        table.values.first().isInt("key", 12)
    }

    @Test
    fun `parses key and boolean value pair`() {
        val parser = TomlParser()
        val table = parser.parse("""key=true""")
        assertEquals(1, table.values.size)
        table.values.first().isBoolean("key", true)
    }

    @Test
    fun `parses key and array value pair`() {
        val parser = TomlParser()
        val table = parser.parse("""key=[1, false]""")
        assertEquals(1, table.values.size)
        table.values.first().apply {
            assertEquals("key", name)
            assertIs<TomlArray>(value)
            assertEquals(2, value.value.size)
            value.value[0].isInt(1)
            value.value[1].isBoolean(false)
        }
    }

    @Test
    fun `parses key value pair surrounded by whitespace`() {
        val parser = TomlParser()
        val table = parser.parse(
            """

   key   =   "value"
              
        """
        )
        assertEquals(1, table.values.size)
        table.values.first().isString("key", "value")
    }

    @Test
    fun `parses dotted key value pair`() {
        val parser = TomlParser()
        val table = parser.parse(
            """
table.key = "value"            
        """.trimIndent()
        )
        assertEquals(1, table.values.size)
        table.values.first().apply {
            assertEquals("table", name)
            assertIs<Table>(value)
            value.apply {
                assertEquals(1, values.size)
                values.first().isString("key", "value")
            }
        }
    }

    @Test
    fun `parses table`() {
        val parser = TomlParser()
        val table = parser.parse(
            """
[table]
key = "value"            
        """.trimIndent()
        )
        assertEquals(1, table.values.size)
        table.values.first().apply {
            assertEquals("table", name)
            assertIs<Table>(value)
            value.apply {
                assertEquals(1, values.size)
                values.first().isString("key", "value")
            }
        }
    }

    private fun Value.isString(key: String, expected: String) {
        assertEquals(key, name)
        value.isString(expected)
    }

    private fun Value.isInt(key: String, expected: Int) {
        assertEquals(key, name)
        value.isInt(expected)
    }

    private fun Value.isBoolean(key: String, expected: Boolean) {
        assertEquals(key, name)
        value.isBoolean(expected)
    }

    private fun TomlValue.isString(expected: String) {
        assertIs<TomlString>(this)
        assertEquals(expected, value)
    }

    private fun TomlValue.isInt(expected: Int) {
        assertIs<TomlInt>(this)
        assertEquals(expected, value)
    }

    private fun TomlValue.isBoolean(expected: Boolean) {
        assertIs<TomlBoolean>(this)
        assertEquals(expected, value)
    }
}