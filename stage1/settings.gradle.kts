pluginManagement {
    includeBuild("../stage0")
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

include("plugins")
include("settings-plugins")

gradle.rootProject {
    lifecycleTask("clean")
    lifecycleTask("assemble")
    lifecycleTask("check")
    lifecycleTask("build")
    lifecycleTask("verifySamples")
}

fun Project.lifecycleTask(name: String) {
    tasks.register(name) {
        val paths = Callable<Any> { subprojects.mapNotNull { it.tasks.findByName(name) } }
        dependsOn(paths)
    }
}