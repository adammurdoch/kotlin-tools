pluginManagement {
    includeBuild("../local-plugins")
    includeBuild("../base-plugins")
    includeBuild("../launcher-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.included-build")
}

for (f in rootDir.listFiles()) {
    val ignore = listOf(".gradle", ".kotlin", "gradle", "kotlin-js-store")
    if (f.isDirectory && !ignore.contains(f.name)) {
        include(f.name)
    }
}
