pluginManagement {
    includeBuild("stage2")
    includeBuild("base-plugins")
}
plugins {
    id("net.rubygrapefruit.plugins.stage2.included-build")
}

includeBuild("bootstrap-plugins")
includeBuild("local-plugins")
includeBuild("base-libs")
includeBuild("base-plugins")
includeBuild("libs")
includeBuild("launcher-plugins")
includeBuild("test-apps")
