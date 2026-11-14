package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class AppWithNoParametersTest: AbstractParseTest() {
    @Test
    fun `reports app usage`() {
        val parser = app("some_app", params { NoParams() })

        parser.usage("some_app")
        parser.noOptions()
    }
}