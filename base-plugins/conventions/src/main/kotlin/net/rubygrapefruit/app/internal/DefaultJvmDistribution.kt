package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmDistribution
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class DefaultJvmDistribution @Inject constructor(factory: ObjectFactory) : DefaultDistribution(factory), JvmDistribution