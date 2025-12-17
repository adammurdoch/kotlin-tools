pluginManagement {
    includeBuild("../stage0")
    includeBuild("../stage1")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.9.0")
}
