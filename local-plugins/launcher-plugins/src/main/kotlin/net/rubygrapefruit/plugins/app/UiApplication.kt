package net.rubygrapefruit.plugins.app

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface UiApplication {
    val iconFile: RegularFileProperty

    val signingIdentity: Property<String>

    val notarizationProfileName: Property<String>
}