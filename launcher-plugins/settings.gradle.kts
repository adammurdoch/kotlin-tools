pluginManagement {
    includeBuild("../stage2")
    includeBuild("../local-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.stage2.included-build")
}

include("native-launcher")
include("native-jvm-launcher")
include("launcher-plugins")
