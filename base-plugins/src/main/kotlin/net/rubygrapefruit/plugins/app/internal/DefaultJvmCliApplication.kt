package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.JvmApplication
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultJvmCliApplication @Inject constructor(factory: ObjectFactory, providers: ProviderFactory, project: Project) :
    DefaultJvmComponent(project), MutableApplication, MutableJvmApplication, JvmApplication {

    private val dist = factory.newInstance(DefaultDistribution::class.java, "main", true, true)

    final override val distributionContainer = SimpleContainer<DefaultDistribution>()

    override val distribution: Provider<Distribution> = providers.provider { dist }

    override val distributions: Provider<List<Distribution>> = providers.provider { distributionContainer.all }

    override var packaging: JvmApplicationPackaging = JvmApplicationWithExternalJvm()

    override val runtimeModulePath: ConfigurableFileCollection = factory.fileCollection()

    init {
        distributionContainer.add(dist)
    }
}