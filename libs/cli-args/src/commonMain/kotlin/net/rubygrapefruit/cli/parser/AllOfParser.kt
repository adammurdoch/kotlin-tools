package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class AllOfParser<OUT>(
    private val parsers: List<ParserInternal<*>>,
    private val summary: String?,
    private val map: (List<ParsedArguments.Success<*>>) -> OUT
) : AbstractParser<OUT>() {

    override fun edges(consumer: (ParseEdge) -> Unit) {
        val elements = parsers.map { element ->
            val edges = mutableListOf<ParseEdge>()
            element.edges(edges::add)
            ElementNode(element, edges)
        }
        val results = emptyList<ParsedArguments.Success<*>>()
        ElementNodes(elements, results, map).edges(consumer)
    }

    override fun usage(): UsageInternal {
        return AllOfUsage()
    }

    private inner class AllOfUsage : UsageInternal {
        private val usages = parsers.map { it.usage() }

        override val isEmpty: Boolean
            get() = usages.isEmpty()

        override fun options(): List<UsageInternal.OptionUsage> {
            return usages.flatMap { it.options() }
        }

        override fun positionals(): List<UsageInternal.PositionalUsage> {
            return usages.flatMap { it.positionals() }
        }

        override fun writeTo(quoted: Boolean, output: Output) {
            if (usages.size == 1) {
                usages.first().writeTo(quoted, output)
            } else {
                usages.forEachIndexed { index, parser ->
                    if (index > 0) {
                        output.write(" ")
                    }
                    parser.writeTo(true, output)
                }
            }
        }

        override fun writeSummaryTo(output: Output) {
            output.writeNullable(summary)
        }
    }

    private class ElementNodes(val elements: List<ElementNode>, val results: List<ParsedArguments.Success<*>>, val map: (List<ParsedArguments.Success<*>>) -> Any?) : ParseNode {
        fun next(element: ElementNode, next: ParseNode): ParseNode {
            val edges = mutableListOf<ParseEdge>()
            next.edges(edges::add)
            val newElement = ElementNode(element.element, edges)
            val newElements = elements.map { if (it == element) newElement else it }
            return ElementNodes(newElements, results, map)
        }

        override fun edges(consumer: (ParseEdge) -> Unit) {
            var addPositional = true
            for (index in elements.indices) {
                val element = elements[index]
                var elementHasPositional = false
                var elementHasEndOfInput = false
                for (edge in element.edges) {
                    when (edge) {
                        is ParseEdge.NonPositional -> {
                            consumer(ElementNonPositional(element, edge, this))
                        }

                        is ParseEdge.Positional -> {
                            if (addPositional) {
                                consumer(ElementPositional(element, edge, this))
                                elementHasPositional = true
                            }
                        }

                        is ParseEdge.Failed -> {
                            consumer(edge)
                            elementHasPositional = true
                        }

                        is ParseEdge.EndOfInput -> {
                            // Hold back until all elements allow end of input
                            elementHasEndOfInput = true
                        }

                        is ParseEdge.NotEndOfInput -> {
                            if (addPositional) {
                                consumer(edge)
                            }
                        }

                        else -> {
                            consumer(edge)
                        }
                    }
                }
                if (elementHasPositional && !elementHasEndOfInput) {
                    addPositional = false
                }
            }
            if (elements.all { it.endOfInput != null }) {
                consumer(MappedResult(elements.map { it.endOfInput!! }, map))
            }
        }
    }

    private class ElementNode(val element: ParserInternal<*>, val edges: List<ParseEdge>) {
        val endOfInput = edges.filterIsInstance<ParseEdge.EndOfInput>().firstOrNull()
    }

    private class ElementNonPositional(val element: ElementNode, val edge: ParseEdge.NonPositional, val owner: ElementNodes) : ParseEdge.NonPositional {
        override fun maybeClaimUnrecognized(args: InputArgs): ParseEdge.Claimed? {
            return edge.maybeClaimUnrecognized(args)
        }

        override fun next(args: InputArgs): ParseEdge.Transition {
            val transition = edge.next(args)
            return when (transition) {
                is ParseEdge.Transition.NotMatched -> transition
                is ParseEdge.Transition.Matched -> {
                    ParseEdge.Transition.Matched(transition.count, owner.next(element, transition.next))
                }
            }
        }
    }

    private class ElementPositional(val element: ElementNode, val edge: ParseEdge.Positional, val owner: ElementNodes) : ParseEdge.Positional {
        override fun next(args: InputArgs): ParseEdge.Transition {
            val transition = edge.next(args)
            return when (transition) {
                is ParseEdge.Transition.NotMatched -> transition
                is ParseEdge.Transition.Matched -> {
                    ParseEdge.Transition.Matched(transition.count, owner.next(element, transition.next))
                }
            }
        }
    }

    private class MappedResult(val elements: List<ParseEdge.EndOfInput>, val map: (List<ParsedArguments.Success<*>>) -> Any?) : ParseEdge.EndOfInput {
        override fun maybeClaimUnrecognized(args: InputArgs): ParseEdge.Claimed? {
            return elements.firstNotNullOfOrNull { it.maybeClaimUnrecognized(args) }
        }

        override fun value(): Any? {
            return map(elements.map { ParsedArguments.FixedResult(it.value()) })
        }
    }
}