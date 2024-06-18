package net.rubygrapefruit.cli

internal class DefaultNullableIntOption(
    names: List<String>,
    help: String?,
    private val host: Host,
    private val owner: Action
) : AbstractOption<Int?>(names, help, host), NullableOption<Int> {
    override fun whenAbsent(default: Int): Option<Int> {
        val option = DefaultOption(names, help, default, host, IntConverter)
        owner.replace(this, option)
        return option
    }

    override fun convert(flag: String, arg: String?): Result<Int?> {
        return if (arg == null) {
            Result.success(null)
        } else {
            IntConverter.convert("option $flag", arg)
        }
    }
}