pluginManagement {
    includeBuild("../base-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
}

include("download")
include("launcher-plugins")
