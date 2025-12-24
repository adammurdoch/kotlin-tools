pluginManagement {
    includeBuild("../stage2")
    includeBuild("../local-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.stage2.included-build")
}

for (f in rootDir.listFiles()) {
    val ignore = listOf(".gradle", ".kotlin", "gradle", "kotlin-js-store")
    if (f.isDirectory && !ignore.contains(f.name)) {
        include(f.name)
    }
}
