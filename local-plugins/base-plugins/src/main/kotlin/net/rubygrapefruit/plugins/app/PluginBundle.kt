package net.rubygrapefruit.plugins.app

interface PluginBundle {
    fun plugin(id: String, implementation: String)
}