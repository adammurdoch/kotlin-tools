pluginManagement {
    includeBuild("../stage2")
}
plugins {
    id("net.rubygrapefruit.stage2.settings")
    id("net.rubygrapefruit.stage2.included-build")
}

projects {
    downgrade("local-plugins/build-constants")
    downgrade("local-plugins/settings-plugins")
    downgrade("local-plugins/model")
    downgrade("local-plugins/release")
    downgrade("local-plugins/samples")
}

fun projects(config: ProjectBuilder.() -> Unit) {
    val builder = ProjectBuilder(settings.settingsDir)
    builder.config()

    val projects = mutableListOf<DowngradedProject>()

    builder.projects.forEach { spec ->
        include(spec.name)
        val project = project(":${spec.name}")
        project.projectDir = spec.projectDir
        val sourceBuildScript = spec.sourceProjectDir.resolve("build.gradle.kts")
        project.buildFileName = sourceBuildScript.relativeTo(project.projectDir).path
    }

    gradle.rootProject {
        tasks.register("generate") {
            doLast {
                projects.forEach { project ->
                    println("${project.projectDir} -> ${project.sourceProjectDir}")
                    project.projectDir.mkdirs()
                    val targetFile = project.projectDir.resolve("target.txt")
                    targetFile.writeText(project.sourceProjectDir.relativeTo(project.projectDir).path)
                }
            }
        }
    }
}

class ProjectBuilder(val settingsDir: File) {
    val projects = mutableListOf<DowngradedProject>()

    fun downgrade(path: String, name: String = path.substringAfterLast('/')) {
        val projectDir = settingsDir.resolve(path)
        val sourceProjectDir = settingsDir.resolve("../$path")
        projects.add(DowngradedProject(name, projectDir, sourceProjectDir))
    }
}

class DowngradedProject(val name: String, val projectDir: File, val sourceProjectDir: File)