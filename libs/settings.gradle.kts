pluginManagement {
    includeBuild("../local-plugins")
    includeBuild("../base-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.included-build")
}

include("download")
include("stream-io")
include("file-io")
include("file-fixtures")
include("process")
include("process-test")
include("store")
include("cli-args")
include("cli-app")
