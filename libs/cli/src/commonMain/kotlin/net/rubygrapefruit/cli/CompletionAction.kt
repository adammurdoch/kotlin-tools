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
                local context state state_descr line
                typeset -A opt_args
            """.trimIndent()
        )
        print("  _arguments -C")
        for (option in usage.options) {
            for (item in option.items) {
                println(" \\")
                if (item.aliases.size == 1) {
                    print("    '${item.aliases.first()}")
                    if (item.help != null) {
                        print("[${item.help}]")
                    }
                    print("'")
                } else {
                    print("    {${item.aliases.joinToString(",")}}")
                    if (item.help != null) {
                        print("'[${item.help}]'")
                    }
                }
            }
        }
        for (index in usage.positional.indices) {
            val positional = usage.positional[index]
            when (positional) {
                is ParameterUsage -> {
                    println(" \\")
                    print("    '${index + 1}::Parameter:'")
                    if (positional.path) {
                        print("_files")
                    } else {
                        print("( )")
                    }
                }

                is ActionParameterUsage -> {
                    println(" \\")
                    println("    '${index + 1}::Action:(${positional.actions.joinToString(" ") { it.name }})' \\")
                    println("    '*::arg:->args'")

                    println("  case \$line[1] in")
                    for (nested in positional.actions) {
                        println("    ${nested.name})")
                        for (nestedIndex in nested.action.positional.indices) {
                            println("      _arguments '${nestedIndex + 1}::Param:( )'")
                        }
                        println("    ;;")
                    }
                    println("  esac")

                    break
                }
            }
        }
        println()
        println(
            """
            }
        """.trimIndent()
        )
    }
}