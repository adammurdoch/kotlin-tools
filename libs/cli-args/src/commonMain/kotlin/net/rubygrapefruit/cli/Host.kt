package net.rubygrapefruit.cli

internal interface Host {
    @Throws(IllegalArgumentException::class)
    fun validate(name: String, description: String)

    fun marker(name: String): String

    fun isMarker(flag: String): Boolean
}

internal object DefaultHost : Host {
    override fun validate(name: String, description: String) {
        if (isMarker(name)) {
            throw IllegalArgumentException("$name cannot be used as $description")
        }
    }

    override fun marker(name: String): String {
        return if (name.length == 1) {
            "-$name"
        } else {
            "--$name"
        }
    }

    override fun isMarker(flag: String): Boolean {
        return flag.startsWith("-")
    }
}
