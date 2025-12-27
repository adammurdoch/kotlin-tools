pluginManagement {
    includeBuild("../stage2")
    includeBuild("../local-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.stage2.included-build")
    id("net.rubygrapefruit.stage2.test-apps")
}

samples {
}
