pluginManagement {
    includeBuild("../base-plugins")
    includeBuild("../launcher-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.included-build")
}

for (f in rootDir.listFiles()) {
    if (f.isDirectory && f.name != ".gradle" && f.name != "build" && f.name != "gradle") {
        include(f.name)
    }
}
