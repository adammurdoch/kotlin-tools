package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class AllOfParser<OUT>(
    private val parsers: List<ParserInternal<*>>,
    private val summary: String?,
    private val map: (List<ParsedArguments.Success<*>>) -> OUT
) : AbstractParser<OUT>() {
    override fun start(): ParseState<OUT> {
        return State(parsers.map { Element(it.start()) }, map)
    }

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
            var hasPositional = false
            for (index in elements.indices) {
                val element = elements[index]
                for (edge in element.nonPositional) {
                    consumer(ElementNonPositional(element, edge, this))
                }
                if (!hasPositional && element.positional.isNotEmpty()) {
                    for (edge in element.positional) {
                        consumer(ElementPositional(element, edge, this))
                    }
                    hasPositional = true
                }
                for (edge in element.required) {
                    consumer(edge)
                }
            }
            if (elements.all { it.endOfInput != null }) {
                consumer(LazyValue { map(elements.map { ParsedArguments.FixedResult(it.endOfInput!!.value()) }) })
            }
        }
    }

    private class ElementNode(val element: ParserInternal<*>, edges: List<ParseEdge>) {
        val nonPositional = edges.filterIsInstance<ParseEdge.NonPositional>()
        val positional = edges.filterIsInstance<ParseEdge.Positional>()
        val required = edges.filterIsInstance<ParseEdge.NotEndOfInput>()
        val endOfInput = edges.filterIsInstance<ParseEdge.EndOfInput>().firstOrNull()
    }

    private class ElementNonPositional(val element: ElementNode, val edge: ParseEdge.NonPositional, val owner: ElementNodes) : ParseEdge.NonPositional {
        override fun next(args: InputArgs): ParseEdge.Transition {
            val transition = edge.next(args)
            return when (transition) {
                is ParseEdge.Transition.NotMatched -> transition
                is ParseEdge.Transition.Failed -> transition
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
                is ParseEdge.Transition.Failed -> transition
                is ParseEdge.Transition.Matched -> {
                    ParseEdge.Transition.Matched(transition.count, owner.next(element, transition.next))
                }
            }
        }
    }

    private class Element<T>(val state: ParseState<T>)

    private class State<OUT>(val elements: List<Element<*>>, val map: (List<ParsedArguments.Success<*>>) -> OUT) : ParseState<OUT> {
        override val expectingOption: Boolean
            get() = elements.any { it.state.expectingOption }

        override val expectingPositional: Boolean
            get() = elements.any { it.state.expectingPositional }

        override fun maybeParse(args: InputArgs): ParseState.Result {
            for (element in elements) {
                if (element.state.expectingOption) {
                    val result = element.state.maybeParse(args)
                    if (result !is ParseState.NotRecognized) {
                        return result
                    }
                }
            }
            for (element in elements) {
                if (element.state.expectingPositional) {
                    return element.state.maybeParse(args)
                }
            }

            return ParseState.NotRecognized
        }

        override fun finish(index: Int): ParsedArguments<OUT> {
            val results = mutableListOf<ParsedArguments<*>>()
            var failed = false
            for (element in elements) {
                val result = element.state.finish(index)
                results.add(result)
                if (result is ParsedArguments.Failure) {
                    failed = true
                }
            }
            return if (failed) {
                Failure(results)
            } else {
                @Suppress("UNCHECKED_CAST")
                Success(results as List<ParsedArguments.Success<*>>, map)
            }
        }
    }

    private abstract class Result<R : ParsedArguments<*>>(val elements: List<R>) {
        fun maybeClaimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            for (element in elements) {
                val problem = element.claimUnrecognized(args)
                if (problem != null) {
                    return problem
                }
            }
            return null
        }
    }

    private class Success<T>(
        elements: List<ParsedArguments.Success<*>>,
        val map: (List<ParsedArguments.Success<*>>) -> T
    ) : Result<ParsedArguments.Success<*>>(elements), ParsedArguments.Success<T> {

        override fun value(): T {
            return map(elements)
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return maybeClaimUnrecognized(args)
        }
    }

    private class Failure(elements: List<ParsedArguments<*>>) : Result<ParsedArguments<*>>(elements), ParsedArguments.Failure {
        private val failures = elements.filterIsInstance<ParsedArguments.Failure>()

        override fun problems(): List<ProblemAtIndex> {
            val problems = mutableListOf<ProblemAtIndex>()
            for (element in failures) {
                problems.addAll(element.problems())
            }
            return problems
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return maybeClaimUnrecognized(args)
        }
    }
}