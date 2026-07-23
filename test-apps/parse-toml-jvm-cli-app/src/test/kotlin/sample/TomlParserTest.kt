package sample

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
    fun `parses key value pair`() {
        val parser = TomlParser()
        val table = parser.parse("""key="value"""")
        assertEquals(1, table.values.size)
        table.values.first().apply {
            assertEquals("key", name)
            assertEquals("value", value)
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
            assertEquals("value", value)
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
                    assertEquals("value", value)
                }
            }
        }
    }
}