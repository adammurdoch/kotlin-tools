pluginManagement {
    includeBuild("../base-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.included-build")
}

include("download")
include("native-launcher")
include("launcher-plugins")
