pluginManagement {
    includeBuild("../stage1")
}
plugins {
    id("net.rubygrapefruit.plugins.stage1.settings")
}

include("plugins")
include("settings-plugins")

gradle.rootProject {
    tasks.register("assemble")
    tasks.register("check")
}