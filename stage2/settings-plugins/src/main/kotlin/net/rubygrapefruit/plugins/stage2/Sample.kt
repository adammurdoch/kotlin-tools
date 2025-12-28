package net.rubygrapefruit.plugins.stage2

sealed interface Sample {
    val name: String
}

sealed interface Lib : Sample

class JvmLib internal constructor(override val name: String) : Lib

class KmpLib internal constructor(override val name: String) : Lib

sealed interface CliApp : Sample

class JvmCliApp internal constructor(override val name: String) : CliApp

class NativeCliApp internal constructor(override val name: String) : CliApp

sealed interface UiApp : Sample

class JvmUiApp internal constructor(override val name: String) : UiApp

class NativeUiApp internal constructor(override val name: String) : UiApp
