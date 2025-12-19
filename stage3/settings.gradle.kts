pluginManagement {
    includeBuild("../stage1")
    includeBuild("../stage2")
}
plugins {
    id("net.rubygrapefruit.plugins.stage1.settings")
}

downgrade("bootstrap-plugins")
downgrade("local-plugins/model")

fun downgrade(path: String) {
    val name = path.substringAfterLast('/')
    include(name)
    project(":$name").buildFileName = "../../$path/build.gradle.kts"
}
