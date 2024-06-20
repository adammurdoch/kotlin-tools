package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionParameterUsage
import net.rubygrapefruit.cli.OptionUsage
import net.rubygrapefruit.cli.ParameterUsage
import net.rubygrapefruit.file.ElementPath

internal class CompletionAction(val action: CliApp) : Action() {
    override fun run() {
        val usage = action.usage()
        val appName = action.name

        val functionName = appName + "_complete"
        println()
        println(
            """
            compdef $functionName $appName

            function $functionName() {
                local context state state_descr line
                typeset -A opt_args
            """.trimIndent()
        )
        print("  _arguments -C")
        options(usage.options, "    ")
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
                        options(nested.action.options, "        ")
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

    private fun options(options: List<OptionUsage>, indent: String) {
        for (option in options) {
            for (item in option.items) {
                println(" \\")
                print(indent)
                if (item.aliases.size == 1) {
                    print("'${item.aliases.first()}")
                    if (item.help != null) {
                        print("[${item.help}]")
                    }
                    print("'")
                } else {
                    print("{${item.aliases.joinToString(",")}}")
                    if (item.help != null) {
                        print("'[${item.help}]'")
                    }
                }
            }
        }
    }

    private fun parameter(index: Int, parameter: ParameterUsage) {
        print("'${index + 1}::Parameter:")
        if (parameter.type == ElementPath::class) {
            print("_files")
        } else {
            print("( )")
        }
        print("'")
    }
}