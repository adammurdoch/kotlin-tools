package net.rubygrapefruit.parse

internal interface CombinatorSingleInputBuilder<in IN> {
    fun maybeAsSingleInputParser(): SingleInputParser<IN>?
}