package net.rubygrapefruit.cli

internal class ActionSet<T : Action>(
    val options: Map<String, ActionDetails<T>>,
    val named: Map<String, ActionDetails<T>>,
    val default: ActionDetails<T>?
) {
    fun lookup(name: String): Pair<String, ActionDetails<T>>? {
        val withName = named[name]
        if (withName != null) {
            return Pair("action '$name'", withName)
        }
        val withOption = options[name]
        if (withOption != null) {
            return Pair("option $name", withOption)
        }
        return null
    }
}