pluginManagement {
    includeBuild("../plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-apps")
}

for (f in rootDir.listFiles()) {
    if (f.isDirectory && f.name != ".gradle" && f.name != "build" && f.name != "gradle") {
        include(f.name)
    }
}
