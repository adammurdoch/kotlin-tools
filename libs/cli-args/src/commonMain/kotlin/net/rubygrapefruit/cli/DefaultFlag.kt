package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultFlag private constructor(
    private val enableFlags: List<String>,
    private val disableFlags: List<String>,
    private val help: String?,
    default: Boolean
) : Named, Flag {
    private var value: Boolean = default

    override val enableUsage: String
        get() = enableFlags.first()

    constructor(names: List<String>, disableOptions: Boolean, help: String?, host: Host, default: Boolean) :
            this(
                names.map { host.option(it) },
                if (disableOptions) names.mapNotNull { if (it.length == 1) null else host.option("no-$it") } else emptyList(),
                help,
                default
            )

    override fun toString(): String {
        return enableFlags.first()
    }

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return value
    }

    override fun usage(): List<FlagUsage> {
        val enableUsage = listOf(SingleOptionUsage(enableFlags.joinToString(", "), help, enableFlags))
        val disableUsage = if (disableFlags.isNotEmpty()) listOf(SingleOptionUsage(disableFlags.joinToString(", "), null, disableFlags)) else emptyList()
        val usage = (enableFlags + disableFlags).joinToString(", ")
        return listOf(FlagUsage(usage, help, enableUsage + disableUsage))
    }

    override fun accepts(option: String): Boolean {
        return enableFlags.contains(option) || disableFlags.contains(option)
    }

    fun value(value: Boolean) {
        this.value = value
    }

    override fun start(context: ParseContext): ParseState {
        return FlagParseState(this, enableFlags, disableFlags, value)
    }
}