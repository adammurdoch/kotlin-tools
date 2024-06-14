package net.rubygrapefruit.cli

internal interface Host {
    @Throws(IllegalArgumentException::class)
    fun validate(name: String, description: String)

    fun option(name: String): String

    fun isOption(flag: String): Boolean
}

internal object DefaultHost : Host {
    override fun validate(name: String, description: String) {
        if (isOption(name)) {
            throw IllegalArgumentException("$name cannot be used as $description")
        }
    }

    override fun option(name: String): String {
        return "--$name"
    }

    override fun isOption(flag: String): Boolean {
        return flag.startsWith("-")
    }
}
