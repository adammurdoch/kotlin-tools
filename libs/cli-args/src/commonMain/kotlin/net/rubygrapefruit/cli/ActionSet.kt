package net.rubygrapefruit.cli

internal class ActionSet<T : Action>(
    val options: Map<String, ChoiceDetails<T>>,
    val named: Map<String, ChoiceDetails<T>>,
    val default: ChoiceDetails<T>?
)