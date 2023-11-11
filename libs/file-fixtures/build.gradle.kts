plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm()
    nativeDesktop()
    common {
        api(kotlin("test"))
        api(project(":file-io"))
    }
}
