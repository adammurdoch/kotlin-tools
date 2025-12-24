pluginManagement {
    includeBuild("stage2")
}
plugins {
    id("net.rubygrapefruit.stage2.included-build")
}

includeBuild("local-plugins")
includeBuild("libs")
includeBuild("test-apps")
