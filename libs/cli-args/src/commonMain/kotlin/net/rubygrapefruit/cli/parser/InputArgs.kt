package net.rubygrapefruit.cli.parser

internal class InputArgs(val index: Int, private val args: List<String>) {
    val length: Int
        get() = args.size - index

    val isEmpty: Boolean
        get() = index == args.size

    fun first(): String {
        return args[index]
    }

    fun second(): String {
        return args[index + 1]
    }

    fun drop(count: Int): InputArgs {
        return if (count == 0) {
            this
        } else {
            InputArgs(index + count, args)
        }
    }
}