plugins {
    id("net.rubygrapefruit.stage2.jvm.lib")
}

library {
    targetJvmVersion = buildConstants.plugins.jvm.version
}

dependencies {
    implementation(project(":cpu-info"))
}