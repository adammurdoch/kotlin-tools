package net.rubygrapefruit.plugins.stage2

sealed interface Sample {
    val name: String
}

class JvmLib internal constructor(override val name: String) : Sample

class KmpLib internal constructor(override val name: String) : Sample

class JvmCliApp internal constructor(override val name: String) : Sample

class NativeCliApp internal constructor(override val name: String) : Sample

class JvmUiApp internal constructor(override val name: String) : Sample

class NativeUiApp internal constructor(override val name: String) : Sample
