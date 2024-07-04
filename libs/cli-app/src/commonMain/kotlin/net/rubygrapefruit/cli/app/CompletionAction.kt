package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.*
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.ElementPath
import net.rubygrapefruit.file.RegularFile
import kotlin.reflect.KClass

internal class CompletionAction(
    private val name: String,
    private val action: Action
) : Action() {
    val formatted: String
        get() {
            val builder = StringBuilder()
            val usage = action.usage().effective()
            val appName = name

            val functionName = appName + "_complete"
            builder.append("\n")
            builder.append(
                """
            compdef $functionName $appName

            function $functionName() {
              local context state state_descr line
              typeset -A opt_args

            """.trimIndent()
            )
            builder.append("  _arguments")
            if (usage.positional.any { it is ActionParameterUsage }) {
                builder.append(" -C")
            }
            builder.options(usage.options, "    ")
            for (index in usage.positional.indices) {
                val positional = usage.positional[index]
                when (positional) {
                    is ParameterUsage -> {
                        builder.append(" \\\n")
                        builder.append("    ")
                        builder.parameter(index, positional)
                        if (positional.multiple) {
                            break
                        }
                    }

                    is ActionParameterUsage -> {
                        builder.append(" \\\n")
                        builder.append("    '${index + 1}::Action:(${positional.named.joinToString(" ") { it.name }})' \\\n")
                        builder.append("    '*::arg:->args'\n")

                        builder.append("  case \$line[1] in\n")
                        for (nested in positional.named) {
                            builder.append("    ${nested.name})\n")
                            builder.append("      _arguments")
                            builder.options(nested.action.options, "        ")
                            for (nestedIndex in nested.action.positional.indices) {
                                val nestedPositional = nested.action.positional[nestedIndex]
                                when (nestedPositional) {
                                    is ParameterUsage -> {
                                        builder.append(" \\\n")
                                        builder.append("        ")
                                        builder.parameter(nestedIndex, nestedPositional)
                                        if (nestedPositional.multiple) {
                                            break
                                        }
                                    }

                                    is ActionParameterUsage -> {
                                        break
                                    }
                                }
                            }
                            builder.append("\n")
                            builder.append("    ;;\n")
                        }
                        builder.append("  esac\n")
                        break
                    }
                }
            }
            builder.append("}\n")
            return builder.toString()
        }

    override fun run() {
        for (line in formatted.lines()) {
            println(line)
        }
    }

    private fun StringBuilder.options(options: List<OptionUsage>, indent: String) {
        for (option in options) {
            for (item in option.usages) {
                append(" \\\n")
                append(indent)
                append("'")
                if (item.aliases.size == 1) {
                    append(item.aliases.first())
                    if (item.help != null) {
                        append("[${item.help}]")
                    }
                } else {
                    append("{${item.aliases.joinToString(",")}}")
                    if (item.help != null) {
                        append("[${item.help}]")
                    }
                }
                if (option.type != null) {
                    append(":Argument:")
                    valueType(option.type, emptyList())
                }
                append("'")
            }
        }
    }

    private fun StringBuilder.parameter(index: Int, parameter: ParameterUsage) {
        append("'")
        when (parameter.cardinality) {
            is Cardinality.Optional -> append("${index + 1}::Parameter:")
            is Cardinality.Required -> append("${index + 1}:Parameter:")
            is Cardinality.ZeroOrMore, is Cardinality.OneOrMore -> append("*:Parameter:")
        }
        valueType(parameter.type, parameter.values)
        append("'")
    }

    private fun StringBuilder.valueType(type: KClass<*>?, candidates: List<String>) {
        if (type.fileType) {
            append("_files")
        } else if (candidates.isNotEmpty()) {
            append("(${candidates.joinToString(" ")})")
        } else {
            append("( )")
        }
    }
}

private val KClass<*>?.fileType: Boolean
    get() = this == ElementPath::class || this == Directory::class || this == RegularFile::class