pluginManagement {
    includeBuild("../bootstrap-plugins")
}
plugins {
    id("net.rubygrapefruit.bootstrap.settings")
    id("net.rubygrapefruit.bootstrap.included-build")
}

includeBuild("../base-libs")
