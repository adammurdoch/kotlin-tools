pluginManagement {
    includeBuild("base-plugins")
}
plugins {
    id("net.rubygrapefruit.included-build")
}

includeBuild("base-plugins")
includeBuild("launcher-plugins")
includeBuild("test-apps")
