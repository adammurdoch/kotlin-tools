plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm {
        module.name = "net.rubygrapefruit.cli-args"
        targetJavaVersion = 11
    }
    nativeDesktop()
    test {
        implementation(kotlin("test"))
    }
}