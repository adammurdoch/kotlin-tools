package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.*
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.ElementPath
import net.rubygrapefruit.file.RegularFile

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
                    if (positional.multiple) {
                        break
                    }
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
                                    if (nestedPositional.multiple) {
                                        break
                                    }
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
            for (item in option.usages) {
                println(" \\")
                print(indent)
                if (item.aliases.size == 1) {
                    print("'${item.aliases.first()}")
                    if (item.help != null) {
                        print("[${item.help}]")
                    }
                } else {
                    print("{${item.aliases.joinToString(",")}}")
                    print("'")
                    if (item.help != null) {
                        print("[${item.help}]")
                    }
                }
                if (option.type != null) {
                    print(":Argument:( )")
                }
                print("'")
            }
        }
    }

    private fun parameter(index: Int, parameter: ParameterUsage) {
        print("'")
        when (parameter.cardinality) {
            is Cardinality.Optional -> print("${index + 1}::Parameter:")
            is Cardinality.Required -> print("${index + 1}:Parameter:")
            is Cardinality.ZeroOrMore, is Cardinality.OneOrMore -> print("*:Parameter:")
        }
        if (parameter.type == ElementPath::class || parameter.type == Directory::class || parameter.type == RegularFile::class) {
            print("_files")
        } else {
            print("( )")
        }
        print("'")
    }
}