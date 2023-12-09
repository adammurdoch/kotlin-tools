pluginManagement {
    includeBuild("../base-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.included-build")
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}

include("download")
include("file-io")
include("file-fixtures")
include("process")
