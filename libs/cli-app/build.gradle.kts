plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm {
        module.name = "net.rubygrapefruit.cli-app"
        targetJavaVersion = 11
    }
    nativeDesktop()
    test {
        implementation(kotlin("test"))
        implementation(project(":file-fixtures"))
    }
    common {
        api(project(":file-io"))
        api(project(":cli-args"))
    }
}