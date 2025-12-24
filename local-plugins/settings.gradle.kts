pluginManagement {
    includeBuild("../stage2")
    includeBuild("../stage3")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.stage2.included-build")
}

include("build-constants")
include("basics")
include("bytecode")
include("machine-info")
include("cpu-info")
include("settings-plugins")
include("model")
include("samples")
include("documentation")
include("release")
include("base-plugins")
include("native-launcher")
include("native-jvm-launcher")
include("launcher-plugins")
