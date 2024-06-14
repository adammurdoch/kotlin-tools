package net.rubygrapefruit.cli

internal interface Host {
    fun option(name: String): String

    fun isOption(flag: String): Boolean
}

internal object DefaultHost : Host {
    override fun option(name: String): String {
        return "--$name"
    }

    override fun isOption(flag: String): Boolean {
        return flag.startsWith("-")
    }
}
