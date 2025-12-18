pluginManagement {
    includeBuild("../stage1")
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

include("plugins")
include("settings-plugins")
