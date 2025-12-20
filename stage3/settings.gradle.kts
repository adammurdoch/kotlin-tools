pluginManagement {
    includeBuild("../stage2")
}
plugins {
    id("net.rubygrapefruit.stage2.settings")
}

downgrade("bootstrap-plugins/build-constants")
downgrade("bootstrap-plugins/settings-plugins")
downgrade("local-plugins/model")

fun downgrade(path: String) {
    val name = path.substringAfterLast('/')
    include(name)
    val project = project(":$name")
    project.projectDir = file(path)
    val sourceBuildScript = file("../$path/build.gradle.kts")
    project.buildFileName = sourceBuildScript.relativeTo(project.projectDir).path
}
