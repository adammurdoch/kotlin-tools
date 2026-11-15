package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OptionTest: AbstractParseTest() {
    @Test
    fun `name must be well formed`() {
        val candidates = listOf(
            "" to "Option name cannot be empty",
            "-"  to "Option name '-' cannot start with '-'",
            "-x" to "Option name '-x' cannot start with '-'",
            "two words" to "Option name 'two words' cannot contain whitespace"
        )

        for ((name, expected) in candidates) {
            assertFailsWith<IllegalArgumentException> {
                option(name)
            }.apply {
                assertEquals(expected, message)
            }
            assertFailsWith<IllegalArgumentException> {
                option("ok", name)
            }.apply {
                assertEquals(expected, message)
            }
        }
    }
}