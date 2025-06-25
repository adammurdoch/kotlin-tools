package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.NativeMachine
import java.io.File

internal class ApplicationMetadata(
    val appName: String,
    val devDistribution: DistributionMetadata?,
    val releaseDistribution: DistributionMetadata?,
    val distributions: List<DistributionMetadata>,
    val installations: List<InstallationMetadata>
)

internal class DistributionMetadata(
    val name: String,
    val canBuildOnHostMachine: Boolean,
    val targetMachine: NativeMachine?,
    val buildType: BuildType?,
    val launcherType: String,
    val hasEmbeddedJvm: Boolean,
    val distTask: String,
    val imageDirectory: File,
    val launcher: File
)

internal class InstallationMetadata(
    val imageDirectory: File,
    val launcher: File
)

internal fun Application.metadata(): ApplicationMetadata {
    val distributionsById = distributions.get().filterIsInstance<MutableDistribution>().associateWith { dist ->
        val targetMachine = if (dist is HasTargetMachine) dist.targetMachine else null
        val buildType = if (dist is HasTargetMachine) dist.buildType else null
        val hasEmbeddedJvm = dist is HasEmbeddedJvm
        val imageDir = dist.imageOutputDirectory.get()
        DistributionMetadata(
            dist.name,
            dist.canBuildOnHostMachine,
            targetMachine,
            buildType,
            launcherFor(dist),
            hasEmbeddedJvm,
            dist.distTask.get().path,
            imageDir.asFile,
            imageDir.file(dist.effectiveLauncherFilePath.get()).asFile
        )
    }
    val devDistribution = devDistribution.get()
    val releaseDistribution = releaseDistribution.get()
    val mappedInstallations = installations.get().filterIsInstance<MutableInstallation>().map { installation ->
        InstallationMetadata(
            installation.imageOutputDirectory.get().asFile,
            installation.launcherOutputFile.get().asFile
        )
    }
    return ApplicationMetadata(
        appName.get(),
        if (devDistribution != null) distributionsById[devDistribution] else null,
        if (releaseDistribution != null) distributionsById[releaseDistribution] else null,
        distributionsById.values.toList(),
        mappedInstallations
    )
}

private fun launcherFor(distribution: Distribution): String {
    return when (distribution) {
        is HasLauncherScripts -> "Scripts"
        is HasLauncherExecutable -> "Executable"
        else -> "No launcher"
    }
}
