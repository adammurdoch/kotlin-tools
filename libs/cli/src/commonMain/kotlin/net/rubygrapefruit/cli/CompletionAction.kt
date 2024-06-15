package net.rubygrapefruit.cli

internal class CompletionAction(val action: MainAction) : Action() {
    override fun run() {
        val usage = action.usage()
        if (usage.appName == null) {
            return
        }

        val functionName = usage.appName + "_complete"
        println()
        println(
            """
            compdef $functionName ${usage.appName}

            function $functionName() {
            """.trimIndent()
        )
        print("  _arguments")
        for (index in usage.positional.indices) {
            for (option in usage.options) {
                println(" \\")
                print("    '${option.usage}")
                if (option.help != null) {
                    print("[${option.help}]")
                }
                print("'")
            }
            val positional = usage.positional[index]
            if (positional.actions.isNotEmpty()) {
                println(" \\")
                print("    '${index + 1}::Action:(${positional.actions.joinToString(" ") { it.name }})'")
            }
        }
        println()
        println("}")
        println()
    }
}