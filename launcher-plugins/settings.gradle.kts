pluginManagement {
    includeBuild("../base-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.included-build")
}

include("native-launcher")
include("launcher-plugins")
include("lifecycle-plugins")
