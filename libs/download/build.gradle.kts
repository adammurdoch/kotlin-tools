plugins {
    id("net.rubygrapefruit.jvm.lib")
}

group = versions.libs.group

library {
    module.name = "net.rubygrapefruit.tools.download"
    targetJvmVersion = 11
    dependencies {
        implementation(versions.libs.coordinates("machine-info"))
    }
}
