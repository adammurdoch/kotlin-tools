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
                    print("    ")
                    parameter(index, positional)
                }

                is ActionParameterUsage -> {
                    println(" \\")
                    println("    '${index + 1}::Action:(${positional.actions.joinToString(" ") { it.name }})' \\")
                    println("    '*::arg:->args'")

                    println("  case \$line[1] in")
                    for (nested in positional.actions) {
                        println("    ${nested.name})")
                        print("      _arguments")
                        for (nestedIndex in nested.action.positional.indices) {
                            val nestedPositional = nested.action.positional[nestedIndex]
                            when (nestedPositional) {
                                is ParameterUsage -> {
                                    println(" \\")
                                    print("        ")
                                    parameter(nestedIndex, nestedPositional)
                                }

                                is ActionParameterUsage -> {
                                    break
                                }
                            }
                        }
                        println()
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

    private fun parameter(index: Int, parameter: ParameterUsage) {
        print("'${index + 1}::Parameter:")
        if (parameter.path) {
            print("_files")
        } else {
            print("( )")
        }
        print("'")
    }
}