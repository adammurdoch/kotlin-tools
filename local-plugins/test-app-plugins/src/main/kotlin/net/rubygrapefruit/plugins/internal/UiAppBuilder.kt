package net.rubygrapefruit.plugins.internal

sealed class UiAppBuilder

class JvmUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : UiAppBuilder() {
    fun derive(name: String) {
        container.add(JvmUiApp(name))
    }

    internal fun register(): JvmUiApp {
        return container.add(JvmUiApp(name))
    }
}

class NativeUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : UiAppBuilder() {
    fun derive(name: String) {
        container.add(NativeUiApp(name))
    }

    internal fun register(): NativeUiApp {
        return container.add(NativeUiApp(name))
    }
}