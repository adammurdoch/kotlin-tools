package net.rubygrapefruit.plugins.app.internal

class SimpleContainer<T> {
    private val elements = mutableListOf<T>()
    private val actions = mutableListOf<(T) -> Unit>()

    val all: List<T>
        get() = elements

    fun add(t: T) {
        elements.add(t)
        for (action in actions) {
            action(t)
        }
    }

    fun each(action: (T) -> Unit) {
        actions.add(action)
        for (element in elements) {
            action(element)
        }
    }
}