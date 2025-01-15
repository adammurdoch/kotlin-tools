package net.rubygrapefruit.plugins.bootstrap

internal class VersionNumber private constructor(private val components: List<Int>, private val qualifier: String?) {
    constructor(value: String) : this(value.split('.').map(String::toInt), null)

    fun dev() = VersionNumber(components, "dev")

    fun next() = VersionNumber(components.dropLast(1) + (components.last() + 1), null)

    override fun toString(): String {
        val str = components.joinToString(".")
        return if (qualifier != null) {
            "$str-$qualifier"
        } else {
            str
        }
    }
}