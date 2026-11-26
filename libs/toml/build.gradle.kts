plugins {
    id("net.rubygrapefruit.kmp.lib")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = versions.libs.jvm.version
        module.name = "net.rubygrapefruit.toml"
    }
    nativeDesktop()
    browser()

    common {
        api(project(":parse"))
    }
    test {
        implementation(versions.test.coordinates)
    }
}
