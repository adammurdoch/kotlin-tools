package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import javax.inject.Inject

abstract class DefaultOperatingSystemTargetApplication @Inject constructor(
    override val target: OperatingSystem,
    private val componentFactory: ComponentFactory
) : NativeComponent<Dependencies>, MutableComponent, PlatformContribution, HasOsTarget {
    override val main = DefaultSourceSet(target.mainSourceSetName, generatedSource)

    override fun dependencies(config: Dependencies.() -> Unit) {
        main.dependencies.config()
    }

    override fun visitMachines(visitor: (KotlinTargetMachine) -> Unit) {
        target.machines.forEach(visitor)
    }

    override fun createTargets() {
        for (machine in target.machines) {
            componentFactory.created(DefaultNativeMachineTargetApplication(machine))
        }
    }
}