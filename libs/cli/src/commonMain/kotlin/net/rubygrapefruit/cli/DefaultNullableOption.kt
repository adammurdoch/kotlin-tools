package net.rubygrapefruit.cli

internal open class DefaultNullableOption<T : Any>(
    names: List<String>,
    help: String?,
    private val host: Host,
    private val owner: Action,
    private val converter: StringConverter<T>,
) : AbstractOption<T?>(names, help, host), NullableOption<T> {
    override fun whenAbsent(default: T): Option<T> {
        val option = DefaultOption(names, help, default, host, converter)
        owner.replace(this, option)
        return option
    }

    override fun convert(flag: String, arg: String?): Result<T?> {
        return if (arg == null) {
            Result.success(null)
        } else {
            converter.convert("option $flag", arg)
        }
    }
}