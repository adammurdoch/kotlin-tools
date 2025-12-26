plugins {
    id("net.rubygrapefruit.jvm.lib")
}

group = versions.libs.group

library {
    module.name = "net.rubygrapefruit.tools.download"
    // TODO - this should not be required
    module.requires.add("machine.info")
    targetJvmVersion = versions.plugins.jvm.version
    dependencies {
        implementation(versions.libs.coordinates("machine-info"))
    }
}
