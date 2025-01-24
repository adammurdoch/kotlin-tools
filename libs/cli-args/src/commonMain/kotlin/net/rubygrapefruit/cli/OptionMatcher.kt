package net.rubygrapefruit.cli

internal class OptionMatcher<T : Any>(
    private val flags: List<String>,
    private val host: Host,
    private val converter: StringConverter<T>
) : Matcher<T> {
    override fun match(args: List<String>): Matcher.Result<T> {
        val arg = args.first()
        if (!flags.contains(arg)) {
            return Matcher.Nothing()
        }
        if (args.size == 1 || host.isOption(args[1])) {
            return Matcher.Failure(1, ArgParseException("Value missing for option $arg"), expectedMore = true)
        }
        val result = converter.convert("option $arg", args[1])
        if (result.isFailure) {
            return Matcher.Failure(2, result.exceptionOrNull() as ArgParseException, expectedMore = false)
        }
        return Matcher.Success(2, result.getOrThrow())
    }
}