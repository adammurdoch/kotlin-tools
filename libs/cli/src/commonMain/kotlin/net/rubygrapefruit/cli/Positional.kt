package net.rubygrapefruit.cli

internal sealed class Positional {
    abstract fun accept(args: List<String>): Int
    abstract fun missing()

    interface Host {
        fun isOption(flag: String): Boolean
    }

    object DefaultHost : Host {
        override fun isOption(flag: String): Boolean {
            return flag.startsWith("-")
        }
    }
}