package net.rubygrapefruit.plugins.app

interface MultiPlatformComponent<D : Dependencies> {
    /**
     * Configures common dependencies for this component.
     */
    fun common(config: D.() -> Unit)

    /**
     * Configures common test dependencies for this component.
     */
    fun test(config: D.() -> Unit)
}