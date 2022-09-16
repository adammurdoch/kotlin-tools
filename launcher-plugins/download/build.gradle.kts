plugins {
    id("net.rubygrapefruit.jvm.lib")
}

group = "net.rubygrapefruit.libs"

library {
    module.name.set("net.rubygrapefruit.tools.download")
    module.exports.add("net.rubygrapefruit.download")
}
