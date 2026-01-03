plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

include("plugins")

gradle.rootProject {
    tasks.register("clean")
    tasks.register("assemble")
    tasks.register("check")
    tasks.register("verifySamples")
}