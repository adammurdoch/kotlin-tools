package net.rubygrapefruit.app

import org.gradle.api.file.RegularFileProperty

interface JvmUiApplication: JvmApplication {
    val iconFile: RegularFileProperty
}