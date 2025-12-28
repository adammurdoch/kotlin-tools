package net.rubygrapefruit.plugins.internal

sealed interface Sample {
    val name: String
}

sealed interface Lib : Sample

class JvmLib internal constructor(override val name: String) : Lib

class KmpLib internal constructor(override val name: String) : Lib

sealed interface CliApp : Sample {
    val distribution: CliAppDistribution
}

class JvmCliApp internal constructor(override val name: String) : CliApp {
    override val distribution = CliAppDistribution("dist")
}

class NativeCliApp internal constructor(override val name: String) : CliApp {
    override val distribution = CliAppDistribution("dist")
}

sealed interface UiApp : Sample {
    val distribution: UiAppDistribution
}

class JvmUiApp internal constructor(override val name: String) : UiApp {
    override val distribution = UiAppDistribution("dist")
}

class NativeUiApp internal constructor(override val name: String) : UiApp {
    override val distribution = UiAppDistribution("dist")
}
