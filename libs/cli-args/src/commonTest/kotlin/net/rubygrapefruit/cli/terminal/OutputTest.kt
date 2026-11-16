package net.rubygrapefruit.cli.terminal

import kotlin.test.Test
import kotlin.test.assertEquals

class OutputTest {
    @Test
    fun `writes empty definition list`() {
        val output = BufferingOutput()
        output.definitionList {
        }
        assertEquals("", output.text)
    }

    @Test
    fun `writes definition list`() {
        val output = BufferingOutput()
        output.definitionList {
            item {
                term { write("<one>") }
                description { write("description of `one`") }
            }
            item {
                term { write("<a longer item>") }
                description { write("description of a longer item") }
            }
        }
        assertEquals(
            """
|  <one>            description of `one`
|  <a longer item>  description of a longer item
|""".trimMargin(), output.text
        )
    }

    @Test
    fun `writes definition list with empty descriptions`() {
        val output = BufferingOutput()
        output.definitionList {
            item {
                term { write("<one>") }
                description { write("description of `one`") }
            }
            item {
                term { write("<a longer item>") }
            }
        }
        assertEquals(
            """
|  <one>            description of `one`
|  <a longer item>
|""".trimMargin(), output.text
        )
    }
}