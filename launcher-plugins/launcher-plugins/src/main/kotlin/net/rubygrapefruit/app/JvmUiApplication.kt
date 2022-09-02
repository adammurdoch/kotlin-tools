package net.rubygrapefruit.app

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface JvmUiApplication : JvmApplication {
    val iconFile: RegularFileProperty

    val signingIdentity: Property<String>

    val notarizationProfileName: Property<String>
}