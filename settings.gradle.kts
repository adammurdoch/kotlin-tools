pluginManagement {
    includeBuild("bootstrap-plugins")
    includeBuild("base-plugins")
}
plugins {
    id("net.rubygrapefruit.included-build")
}

includeBuild("base-libs")
includeBuild("base-plugins")
includeBuild("libs")
includeBuild("launcher-plugins")
includeBuild("test-apps")
