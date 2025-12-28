package net.rubygrapefruit.plugins.stage2

sealed interface Sample {
    val name: String
}

class JvmLib internal constructor(override val name: String, private val container: SampleContainer) : Sample {
    fun derive(name: String): JvmLib {
        return container.add(JvmLib(name, container))
    }
}

class KmpLib internal constructor(override val name: String, private val container: SampleContainer) : Sample {
    fun derive(name: String): KmpLib {
        return container.add(KmpLib(name, container))
    }
}

class JvmCliApp internal constructor(override val name: String, private val container: SampleContainer) : Sample {
    fun derive(name: String, config: DerivedJvmCliAppBuilder.() -> Unit): JvmCliApp {
        return container.add(JvmCliApp(name, container))
    }

    fun deriveNative(name: String): NativeCliApp {
        return container.add(NativeCliApp(name, container))
    }
}

class NativeCliApp internal constructor(override val name: String, private val container: SampleContainer) : Sample {
    fun derive(name: String, config: DerivedNativeCliAppBuilder.() -> Unit): NativeCliApp {
        return container.add(NativeCliApp(name, container))
    }
}

class JvmUiApp internal constructor(override val name: String, private val container: SampleContainer) : Sample {
    fun derive(name: String): JvmUiApp {
        return container.add(JvmUiApp(name, container))
    }
}

class NativeUiApp internal constructor(override val name: String, private val container: SampleContainer) : Sample {
    fun derive(name: String): NativeUiApp {
        return container.add(NativeUiApp(name, container))
    }
}