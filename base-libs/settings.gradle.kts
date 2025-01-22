pluginManagement {
    includeBuild("../bootstrap-plugins")
    includeBuild("../local-plugins")
}
plugins {
    id("net.rubygrapefruit.bootstrap.settings")
    id("net.rubygrapefruit.bootstrap.included-build")
}

include("basics")
include("bytecode")
include("machine-info")
include("cpu-info")
