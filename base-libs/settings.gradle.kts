pluginManagement {
    includeBuild("../bootstrap-plugins")
}
plugins {
    id("net.rubygrapefruit.bootstrap.settings")
    id("net.rubygrapefruit.bootstrap.included-build")
}

include("bytecode")
include("machine-info")
