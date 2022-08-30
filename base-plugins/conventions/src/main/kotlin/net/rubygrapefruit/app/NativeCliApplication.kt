package net.rubygrapefruit.app

import org.gradle.api.file.RegularFileProperty

interface NativeCliApplication: CliApplication {
    val outputBinary: RegularFileProperty
}