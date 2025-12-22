pluginManagement {
    includeBuild("../stage1")
}
plugins {
    id("net.rubygrapefruit.stage1.settings")
    id("net.rubygrapefruit.stage1.included-build")
}

include("plugins")
include("settings-plugins")
include("stage-dsl-plugins")
