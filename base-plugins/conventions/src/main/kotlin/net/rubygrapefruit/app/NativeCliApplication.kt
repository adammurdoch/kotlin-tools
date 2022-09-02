package net.rubygrapefruit.app

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

interface NativeCliApplication : Application {
    val outputBinary: Provider<RegularFile>
}