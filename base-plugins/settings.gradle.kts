pluginManagement {
    includeBuild("../stage2")
    includeBuild("../bootstrap-plugins")
}
plugins {
    id("net.rubygrapefruit.stage2.settings")
    id("net.rubygrapefruit.stage2.included-build")
}

includeBuild("../base-libs")
