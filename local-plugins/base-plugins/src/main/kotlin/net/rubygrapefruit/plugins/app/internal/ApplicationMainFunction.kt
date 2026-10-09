package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.metadata.MainFunction
import org.gradle.api.provider.Provider

class ApplicationMainFunction(val mainFunction: Provider<MainFunction>)
