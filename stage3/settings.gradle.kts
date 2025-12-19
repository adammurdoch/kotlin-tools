pluginManagement {
    includeBuild("../stage1")
    includeBuild("../stage2")
}
plugins {
    id("net.rubygrapefruit.plugins.stage1.settings")
}

include("bootstrap-plugins")

project(":bootstrap-plugins").buildFileName = "../../bootstrap-plugins/build.gradle.kts"
