plugins {
    id("net.rubygrapefruit.stage2.jvm.lib")
}

library {
    targetJvmVersion = 11
}

dependencies {
    implementation(project(":cpu-info"))
}