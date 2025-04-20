pluginManagement {
    includeBuild("../base-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.included-build")
}

include("native-launcher")
include("native-jvm-launcher")
include("launcher-plugins")
