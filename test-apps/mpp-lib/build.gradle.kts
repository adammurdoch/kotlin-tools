plugins {
    id("net.rubygrapefruit.mpp.lib")
}

library {
    module.name.set("sample.parser")
    module.exports.add("sample.calc")
}
