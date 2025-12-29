package net.rubygrapefruit.plugins.internal

sealed class UiAppBuilder

class JvmUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : UiAppBuilder() {
    fun derive(name: String) {
        container.add(name, ::JvmUiApp)
    }

    internal fun register(): JvmUiApp {
        return container.add(name, ::JvmUiApp)
    }
}

class NativeUiAppBuilder internal constructor(
    private val name: String,
    private val container: SampleContainer
) : UiAppBuilder() {
    fun derive(name: String) {
        container.add(name, ::NativeUiApp)
    }

    internal fun register(): NativeUiApp {
        return container.add(name, ::NativeUiApp)
    }
}