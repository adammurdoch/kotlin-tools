pluginManagement {
    includeBuild("../stage0")
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

include("gradle-plugin-plugin")
