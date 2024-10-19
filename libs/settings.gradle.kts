pluginManagement {
    includeBuild("../base-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.included-build")
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}

include("strings")
include("download")
include("stream-io")
include("file-io")
include("file-fixtures")
include("process")
include("process-test")
include("store")
include("cli-args")
include("cli-app")
