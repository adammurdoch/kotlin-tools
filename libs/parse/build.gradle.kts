plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = versions.libs.jvm.version
        module.name = "net.rubygrapefruit.parse"
    }
    nativeDesktop()
    browser()
    test {
        implementation(versions.test.coordinates)
    }
}
