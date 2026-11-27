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
        table.values.first().apply {
            assertEquals("key", name)
            assertIs<TomlString>(value)
            assertEquals("value", value.value)
        }
    }

    @Test
    fun `parses key and number value pair`() {
        val parser = TomlParser()
        val table = parser.parse("""key=12""")
        assertEquals(1, table.values.size)
        table.values.first().apply {
            assertEquals("key", name)
            assertIs<TomlInt>(value)
            assertEquals(12, value.value)
        }
    }

    @Test
    fun `parses key and boolean value pair`() {
        val parser = TomlParser()
        val table = parser.parse("""key=true""")
        assertEquals(1, table.values.size)
        table.values.first().apply {
            assertEquals("key", name)
            assertIs<TomlBoolean>(value)
            assertEquals(true, value.value)
        }
    }

    @Test
    fun `parses key and array value pair`() {
        val parser = TomlParser()
        val table = parser.parse("""key=[1, true]""")
        assertEquals(1, table.values.size)
        table.values.first().apply {
            assertEquals("key", name)
            assertIs<TomlArray>(value)
            assertEquals(2, value.value.size)
            assertIs<TomlInt>(value.value[0])
            assertIs<TomlBoolean>(value.value[1])
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
        table.values.first().apply {
            assertEquals("key", name)
            assertIs<TomlString>(value)
            assertEquals("value", value.value)
        }
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
                values.first().apply {
                    assertEquals("key", name)
                    assertIs<TomlString>(value)
                    assertEquals("value", value.value)
                }
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
                values.first().apply {
                    assertEquals("key", name)
                    assertIs<TomlString>(value)
                    assertEquals("value", value.value)
                }
            }
        }
    }
}