package net.rubygrapefruit.cli.parser

internal interface InputArgs {
    val index: Int

    fun first(): String
    fun firstIsOption(): Boolean

    fun hasSecond(): Boolean
    fun second(): String
    fun secondIsOption(): Boolean
}

internal class DefaultInputArgs(override val index: Int, private val args: List<String>) : InputArgs {
    val isEmpty: Boolean
        get() = index == args.size

    override fun first(): String {
        return args[index]
    }

    override fun firstIsOption(): Boolean {
        return args[index].startsWith("-")
    }

    override fun second(): String {
        return args[index + 1]
    }

    override fun hasSecond(): Boolean {
        return index + 1 < args.size
    }

    override fun secondIsOption(): Boolean {
        return args[index + 1].startsWith("-")
    }

    fun drop(count: Int): DefaultInputArgs {
        return if (count == 0) {
            this
        } else {
            DefaultInputArgs(index + count, args)
        }
    }
}