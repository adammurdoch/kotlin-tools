package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

class ModuleClasspathInspectionResults(
    val moduleInfoClasspath: Provider<Directory>
)