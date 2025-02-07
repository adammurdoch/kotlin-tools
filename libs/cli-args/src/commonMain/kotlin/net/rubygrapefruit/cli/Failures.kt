package net.rubygrapefruit.cli

internal fun missingParameter(name: String, context: ParseContext): ParseState.FinishResult {
    return ParseState.FinishFailure(
        "Parameter '$name' not provided",
        resolution = "Please provide a value for parameter '$name'.",
        positional = context.positional
    )
}