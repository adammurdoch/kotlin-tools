pluginManagement {
    includeBuild("../base-plugins")
    includeBuild("../launcher-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.included-build")
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}

for (f in rootDir.listFiles()) {
    val ignore = listOf(".gradle", ".kotlin", "gradle")
    if (f.isDirectory && !ignore.contains(f.name)) {
        include(f.name)
    }
}
