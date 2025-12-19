pluginManagement {
    includeBuild("../stage2")
    includeBuild("../base-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.stage2.included-build")
}

include("native-launcher")
include("native-jvm-launcher")
include("launcher-plugins")
