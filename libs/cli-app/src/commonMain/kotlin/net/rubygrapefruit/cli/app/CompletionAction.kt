package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.*
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.ElementPath
import net.rubygrapefruit.file.RegularFile
import kotlin.reflect.KClass

internal class CompletionAction(
    private val name: String,
    private val action: Action,
    private val formatter: Formatter
) : Action() {

    override fun run() {
        val usage = action.usage().effective()
        val appName = name

        formatter.run {
            val functionName = appName + "_complete"

            newLine()
            appendln("compdef $functionName $appName")
            newLine()
            appendln("function $functionName() {")
            indent {
                appendln(
                    """
                  local context state state_descr line
                  typeset -A opt_args
                """.trimIndent()
                )
                completion(usage)
            }
            appendln("}")
            newLine()
        }
    }

    private fun Formatter.completion(action: ActionUsage) {
        if (action.positional.isEmpty() && action.options.isEmpty()) {
            return
        }
        append("_arguments")
        if (action.positional.any { it is ActionParameterUsage }) {
            append(" -C")
        }
        indent {
            options(action.options)
            for (index in action.positional.indices) {
                val positional = action.positional[index]
                appendln(" \\")
                when (positional) {
                    is ParameterUsage -> {
                        parameter(index, positional)
                        if (positional.cardinality.multiple) {
                            break
                        }
                    }

                    is LiteralUsage -> {
                        append("'${index + 1}:Literal:(${positional.name})")
                    }

                    is ActionParameterUsage -> {
                        appendln("'${index + 1}:Action:(${positional.named.joinToString(" ") { it.name }})' \\")
                        appendln("'*::arg:->args'")
                        appendln("case \$line[1] in")
                        indent {
                            for (nested in positional.named) {
                                appendln("${nested.name})")
                                indent {
                                    completion(nested.action)
                                    appendln(";;")
                                }
                            }
                        }
                        appendln("esac")
                        break
                    }
                }
            }
        }
        maybeNewLine()
    }

    private fun Formatter.options(options: List<NonPositionalUsage>) {
        for (option in options) {
            for (item in option.choices) {
                appendln(" \\")
                if (item.names.size == 1) {
                    append("'")
                    append(item.names.first())
                    if (item.help != null) {
                        quoted("[${item.help}]")
                    }
                } else {
                    append("{${item.names.joinToString(",")}}'")
                    if (item.help != null) {
                        quoted("[${item.help}]")
                    }
                }
                if (option is OptionUsage) {
                    append(":Argument:")
                    valueType(option.type, emptyList())
                }
                append("'")
            }
        }
    }

    private fun Formatter.parameter(index: Int, parameter: ParameterUsage) {
        append("'")
        when (parameter.cardinality) {
            is Cardinality.Optional -> append("${index + 1}::Parameter:")
            is Cardinality.Required -> append("${index + 1}:Parameter:")
            is Cardinality.ZeroOrMore, is Cardinality.OneOrMore -> append("*:Parameter:")
        }
        valueType(parameter.type, parameter.values)
        append("'")
    }

    private fun Formatter.valueType(type: KClass<*>?, candidates: List<String>) {
        if (type.fileType) {
            append("_files")
        } else if (candidates.isNotEmpty()) {
            append("(${candidates.joinToString(" ")})")
        } else {
            append("( )")
        }
    }

    private fun Formatter.quoted(text: String) {
        append(text.replace("'", """'"'"'"""))
    }
}

private val KClass<*>?.fileType: Boolean
    get() = this == ElementPath::class || this == Directory::class || this == RegularFile::class