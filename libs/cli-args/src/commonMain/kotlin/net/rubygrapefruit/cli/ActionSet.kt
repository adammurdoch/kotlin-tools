package net.rubygrapefruit.cli

internal class ActionSet<T : Action>(
    val options: Map<String, ActionDetails<T>>,
    val named: Map<String, ActionDetails<T>>,
    val default: ActionDetails<T>?
)