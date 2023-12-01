plugins {
    id("net.rubygrapefruit.jvm.lib")
}

group = "net.rubygrapefruit.libs"

library {
    module.name = "net.rubygrapefruit.tools.download"
    // TODO - this should not be required
    module.requires.add("machine.info")
    targetJavaVersion = versions.pluginsJava
    dependencies {
        implementation("net.rubygrapefruit.libs:machine-info:any")
    }
}
