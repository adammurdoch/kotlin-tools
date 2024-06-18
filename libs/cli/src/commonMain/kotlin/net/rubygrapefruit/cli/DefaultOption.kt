package net.rubygrapefruit.cli

internal class DefaultOption<T : Any>(
    names: List<String>,
    help: String?,
    val default: T,
    host: Host,
    private val converter: StringConverter<T>
) : AbstractOption<T>(names, help, host), Option<T> {
    override fun convert(flag: String, arg: String?): Result<T> {
        return if (arg == null) {
            Result.success(default)
        } else {
            converter.convert("option $flag", arg)
        }
    }
}