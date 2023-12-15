plugins {
    id("net.rubygrapefruit.kmp.lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm {
        targetJavaVersion = versions.pluginsJava
    }
}