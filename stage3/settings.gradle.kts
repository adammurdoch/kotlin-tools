pluginManagement {
    includeBuild("../stage2")
}
plugins {
    id("net.rubygrapefruit.stage2.settings")
}

projects {
    downgrade("local-plugins/build-constants")
    downgrade("local-plugins/settings-plugins")
    downgrade("local-plugins/model")
    downgrade("local-plugins/release")
    downgrade("local-plugins/samples")
}

fun projects(config: ProjectBuilder.() -> Unit) {
    val builder = ProjectBuilder()
    builder.config()

    val projects = mutableListOf<DowngradedProject>()

    builder.projects.forEach { path ->
        val name = path.substringAfterLast('/')
        include(name)
        val project = project(":$name")
        project.projectDir = file(path)
        val sourceProjectDir = file("../$path")
        val sourceBuildScript = sourceProjectDir.resolve("build.gradle.kts")
        project.buildFileName = sourceBuildScript.relativeTo(project.projectDir).path
        projects.add(DowngradedProject(project.projectDir, sourceProjectDir))
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

class ProjectBuilder {
    val projects = mutableListOf<String>()

    fun downgrade(path: String) {
        projects.add(path)
    }
}

class DowngradedProject(val projectDir: File, val sourceProjectDir: File)