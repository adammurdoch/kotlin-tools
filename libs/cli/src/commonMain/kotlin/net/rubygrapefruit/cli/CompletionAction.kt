package net.rubygrapefruit.cli

internal class CompletionAction(val action: MainAction) : Action() {
    override fun run() {
        val usage = action.usage()
        if (usage.appName == null) {
            return
        }
        val actions = usage.positional.flatMap { it.actions }

        val functionName = usage.appName + "_complete"
        println()
        println("compdef $functionName ${usage.appName}")
        println()
        println(
            """
            function $functionName() {
                _arguments '1::Action:(${actions.joinToString(" ") { it.name }})'
            }
        """.trimIndent()
        )
    }
}