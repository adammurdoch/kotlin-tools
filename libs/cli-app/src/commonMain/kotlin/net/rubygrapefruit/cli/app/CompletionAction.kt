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
            append(
                """
                compdef $functionName $appName
    
                function $functionName() {
                  local context state state_descr line
                  typeset -A opt_args
    
                """.trimIndent()
            )
            completion(usage, "  ")
            append("}")
            newLine()
        }
    }

    private fun Formatter.completion(action: ActionUsage, indent: String) {
        if (action.positional.isEmpty() && action.options.isEmpty()) {
            return
        }
        append(indent)
        append("_arguments")
        if (action.positional.any { it is ActionParameterUsage }) {
            append(" -C")
        }
        val nestedIndent = "$indent  "
        options(action.options, nestedIndent)
        for (index in action.positional.indices) {
            val positional = action.positional[index]
            when (positional) {
                is ParameterUsage -> {
                    append(" \\\n")
                    append(nestedIndent)
                    parameter(index, positional)
                    if (positional.cardinality.multiple) {
                        break
                    }
                }

                is ActionParameterUsage -> {
                    append(" \\\n")
                    append(nestedIndent)
                    append("'${index + 1}::Action:(${positional.named.joinToString(" ") { it.name }})' \\\n")
                    append(nestedIndent)
                    append("'*::arg:->args'\n")

                    append(indent)
                    append("case \$line[1] in\n")
                    val caseIndent = "$nestedIndent  "
                    for (nested in positional.named) {
                        append(nestedIndent)
                        append("${nested.name})\n")
                        completion(nested.action, caseIndent)
                        append(caseIndent)
                        append(";;\n")
                    }
                    append(indent)
                    append("esac\n")
                    break
                }
            }
        }
        maybeNewLine()
    }

    private fun Formatter.options(options: List<NonPositionalUsage>, indent: String) {
        for (option in options) {
            for (item in option.choices) {
                append(" \\\n")
                append(indent)
                if (item.aliases.size == 1) {
                    append("'")
                    append(item.aliases.first())
                    if (item.help != null) {
                        append("[${item.help}]")
                    }
                } else {
                    append("{${item.aliases.joinToString(",")}}'")
                    if (item.help != null) {
                        append("[${item.help}]")
                    }
                }
                if (option is OptionUsage && option.type != null) {
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
}

private val KClass<*>?.fileType: Boolean
    get() = this == ElementPath::class || this == Directory::class || this == RegularFile::class