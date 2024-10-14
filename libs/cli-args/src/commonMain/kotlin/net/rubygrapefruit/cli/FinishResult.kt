package net.rubygrapefruit.cli

sealed class FinishResult {
    data object Success : FinishResult()

    data class Failure(val failure: ArgParseException, val expectedMore: Boolean = false) : FinishResult()
}