import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.jvm.lib")
}

group = Versions.libs.group

library {
    module.name = "net.rubygrapefruit.tools.download"
    // TODO - this should not be required
    module.requires.add("machine.info")
    targetJvmVersion = Versions.plugins.java
    dependencies {
        implementation(Versions.libs.coordinates("machine-info"))
    }
}
