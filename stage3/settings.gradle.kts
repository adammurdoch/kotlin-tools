import org.gradle.kotlin.dsl.getByName
import kotlin.jvm.java

pluginManagement {
    includeBuild("../stage1")
    includeBuild("../stage2")
}
plugins {
    id("net.rubygrapefruit.stage2.settings")
    id("net.rubygrapefruit.stage2.included-build")
    id("net.rubygrapefruit.stage2.stage-dsl")
}

projects {
    upgrade("stage2/plugins")
    upgrade("stage2/settings-plugins", "stage2-settings-plugins")
    downgrade("local-plugins/build-constants")
    downgrade("local-plugins/settings-plugins")
    downgrade("local-plugins/model")
    downgrade("local-plugins/release")
    downgrade("local-plugins/samples")
}

fun projects(config: ProjectBuilder.() -> Unit) {
    val builder = ProjectBuilder(settings.settingsDir)
    builder.config()

    builder.projects.forEach { spec ->
        include(spec.name)
        val project = project(spec.path)
        project.projectDir = spec.projectDir
        val sourceBuildScript = spec.sourceProjectDir.resolve("build.gradle.kts")
        project.buildFileName = sourceBuildScript.relativeTo(project.projectDir).path
    }

    gradle.rootProject {
        builder.projects.forEach { project ->
            project(project.path) {
                applyKotlinSourceFromTargetProject(project)
            }
        }
        tasks.register("generate") {
            doLast {
                builder.projects.forEach { project ->
                    println("${project.projectDir} -> ${project.sourceProjectDir}")
                    project.projectDir.mkdirs()
                    val targetFile = project.projectDir.resolve("target.txt")
                    targetFile.writeText(project.sourceProjectDir.relativeTo(project.projectDir).path)
                }
            }
        }
    }
}

fun Project.applyKotlinSourceFromTargetProject(spec: DowngradedProject) {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        val sourceDirProvider = provider {
            val sourceDir = spec.sourceProjectDir.resolve("src/main/kotlin")
            if (sourceDir.exists()) {
                println("-> REDIRECT SOURCE FOR $this -> ${sourceDir}")
                sourceDir
            } else {
                emptyList<File>()
            }
        }
//        val kotlin = extensions.getByType(KotlinProjectExtension::class.java)
//        kotlin.sourceSets.getByName("main").kotlin.srcDir(sourceDirProvider)
        sourceDirProvider.get()

        afterEvaluate {
            group = "stage3"
        }
    }
}

class ProjectBuilder(val settingsDir: File) {
    val projects = mutableListOf<DowngradedProject>()

    fun upgrade(path: String, name: String = path.substringAfterLast('/')) {
        downgrade(path, name)
    }

    fun downgrade(path: String, name: String = path.substringAfterLast('/')) {
        val projectDir = settingsDir.resolve(path)
        val sourceProjectDir = settingsDir.resolve("../$path")
        projects.add(DowngradedProject(name, projectDir, sourceProjectDir))
    }
}

class DowngradedProject(val name: String, val projectDir: File, val sourceProjectDir: File) {
    val path: String get() = ":$name"
}