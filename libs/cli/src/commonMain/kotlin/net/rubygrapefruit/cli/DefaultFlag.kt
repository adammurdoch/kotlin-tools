package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultFlag private constructor(
    private val enableFlags: List<String>,
    private val disableFlags: List<String>,
    private val help: String?,
    default: Boolean,
    private val owner: Action
) : NonPositional(), Flag {
    private var value: Boolean = default

    override val enableUsage: String
        get() = enableFlags.first()

    constructor(names: List<String>, disableOptions: Boolean, help: String?, host: Host, default: Boolean, owner: Action) :
            this(
                names.map { host.option(it) },
                if (disableOptions) names.mapNotNull { if (it.length == 1) null else host.option("no-$it") } else emptyList(),
                help,
                default,
                owner
            )

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return value
    }

    override fun whenAbsent(default: Boolean): Flag {
        val flag = DefaultFlag(enableFlags, disableFlags, help, default, owner)
        owner.replace(this, flag)
        return flag
    }

    override fun usage(): List<OptionUsage> {
        val enableUsage = listOf(SingleOptionUsage(enableFlags.joinToString(", "), help, enableFlags))
        val disableUsage = if (disableFlags.isNotEmpty()) listOf(SingleOptionUsage(disableFlags.joinToString(", "), null, disableFlags)) else emptyList()
        val usage = (enableFlags + disableFlags).joinToString(", ")
        return listOf(
            OptionUsage(usage, help, enableUsage + disableUsage)
        )
    }

    override fun accept(args: List<String>): ParseResult {
        val arg = args.first()
        return if (enableFlags.contains(arg)) {
            value = true
            ParseResult.One
        } else if (disableFlags.contains(arg)) {
            value = false
            ParseResult.One
        } else {
            ParseResult.Nothing
        }
    }
}