plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    nativeDesktop()
    common {
        implementation(project(":stream-io"))
    }
    test {
        implementation(kotlin("test"))
        implementation(project(":file-fixtures"))
    }
}
