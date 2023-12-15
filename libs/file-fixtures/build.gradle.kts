plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm {
        targetJavaVersion = versions.pluginsJava
    }
    nativeDesktop()
    common {
        api(kotlin("test"))
        api(project(":file-io"))
    }
}
