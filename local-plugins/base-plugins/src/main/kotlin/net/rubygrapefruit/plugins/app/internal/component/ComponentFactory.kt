package net.rubygrapefruit.plugins.app.internal.component

interface ComponentFactory {
    fun <T : Any> created(component: T): T
}