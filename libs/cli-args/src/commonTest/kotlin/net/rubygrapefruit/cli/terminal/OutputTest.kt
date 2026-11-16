package net.rubygrapefruit.cli.terminal

import kotlin.test.Test
import kotlin.test.assertEquals

class OutputTest {
    @Test
    fun `writes empty table`() {
        val output = BufferingOutput()
        output.table {
        }
        assertEquals("", output.text)
    }

    @Test
    fun `writes table with empty cells`() {
        val output = BufferingOutput()
        output.table {
            row {
                cell { }
                cell { }
                cell { write("cell3") }
            }
            row {
                cell { write("cell1") }
                cell { write("cell2") }
                cell { }
            }
        }
        assertEquals(
            """
|      cell3
|  cell1  cell2
|""".trimMargin(), output.text
        )
    }
}