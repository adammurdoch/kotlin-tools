package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty

class RealizedNativeComponent(
    override val sourceSetName: String,
    override val generatedSource: SetProperty<Directory>
) : HasGeneratedSource