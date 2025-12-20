pluginManagement {
    includeBuild("../stage2")
}

plugins {
    id("net.rubygrapefruit.stage2.settings")
    id("net.rubygrapefruit.stage2.included-build")
}

include("build-constants")
include("settings-plugins")
