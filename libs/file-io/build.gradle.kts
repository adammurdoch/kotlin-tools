plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm()
    nativeDesktop()
    test {
        implementation(kotlin("test"))
        implementation(project(":file-fixtures"))
    }
}
