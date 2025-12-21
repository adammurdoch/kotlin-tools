pluginManagement {
    includeBuild("../stage2")
    includeBuild("../local-plugins")
}
plugins {
    id("net.rubygrapefruit.stage2.settings")
    id("net.rubygrapefruit.stage2.included-build")
}

include("basics")
include("bytecode")
include("machine-info")
include("cpu-info")
