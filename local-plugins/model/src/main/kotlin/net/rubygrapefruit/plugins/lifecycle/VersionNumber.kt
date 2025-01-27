package net.rubygrapefruit.plugins.lifecycle

class VersionNumber private constructor(private val components: List<Int>, private val qualifier: Qualifier?) {
    companion object {
        fun of(value: String): VersionNumber {
            val pattern = Regex("(\\d+(\\.\\d+)*)(-(\\w+)-(\\d+))?")
            val match = pattern.matchEntire(value)
            if (match == null) {
                throw IllegalArgumentException("Cannot parse version: '$value'")
            }
            val parts = match.groupValues[1].split('.').map { it.toInt() }
            val qualifier = if (match.groupValues.size > 2) {
                Qualifier(match.groupValues[4], match.groupValues[5].toInt())
            } else {
                null
            }
            return VersionNumber(parts, qualifier)
        }
    }

    val prerelease: Boolean get() = qualifier != null

    fun milestone(): VersionNumber {
        val qualifier = if (qualifier != null && qualifier.name == "milestone") {
            qualifier
        } else {
            Qualifier("milestone", 1)
        }
        return VersionNumber(components, qualifier)
    }

    fun final() = VersionNumber(components, null)

    fun nextMilestone(): VersionNumber {
        return if (qualifier == null) {
            VersionNumber(components.dropLast(1) + (components.last() + 1), Qualifier("milestone", 1))
        } else {
            VersionNumber(components, Qualifier("milestone", qualifier.number + 1))
        }
    }

    fun released(): VersionNumber {
        return if (qualifier == null) {
            this
        } else {
            VersionNumber(components.dropLast(1) + (components.last() - 1), null)
        }
    }

    override fun toString(): String {
        val str = components.joinToString(".")
        return if (qualifier != null) {
            "$str-${qualifier.name}-${qualifier.number}"
        } else {
            str
        }
    }

    private class Qualifier(val name: String, val number: Int)
}