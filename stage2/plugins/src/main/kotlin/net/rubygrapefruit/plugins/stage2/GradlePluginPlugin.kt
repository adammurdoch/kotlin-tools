package net.rubygrapefruit.plugins.stage2

import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class GradlePluginPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(BuildConstants.constants.stage1.plugins.gradlePlugin.id)

            group = BuildConstants.constants.production.plugins.group

            val hasTarget= applyKotlinSourceFromTargetProject()
            if (hasTarget) {
                group = "stage3"
            }
        }
    }
}