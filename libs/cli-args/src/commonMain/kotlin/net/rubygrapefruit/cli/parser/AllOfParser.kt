package net.rubygrapefruit.cli.parser

internal class AllOfParser<A, B, OUT>(
    private val a: ParserInternal<A>,
    private val b: ParserInternal<B>,
    private val map: (A, B) -> OUT
) : AbstractParser<OUT>() {
    override fun maybeParse(args: List<String>): ParserInternal.Result<OUT> {
        val aResult = a.maybeParse(args)
        if (aResult is ParserInternal.Success) {
            val bResult = b.maybeParse(args.drop(aResult.count))
            if (bResult is ParserInternal.Success) {
                return ParserInternal.Success(aResult.count + bResult.count, map(aResult.result, bResult.result))
            } else {
                ParserInternal.Failure(listOf(Parser.Problem(0, "??")))
            }
        }
        val bResult = b.maybeParse(args)
        if (bResult is ParserInternal.Success) {
            val aResult = a.maybeParse(args.drop(bResult.count))
            if (aResult is ParserInternal.Success) {
                return ParserInternal.Success(aResult.count + bResult.count, map(aResult.result, bResult.result))
            } else {
                return ParserInternal.Failure(listOf(Parser.Problem(0, "??")))
            }
        }
        return ParserInternal.Failure(listOf(Parser.Problem(0, "??")))
    }
}