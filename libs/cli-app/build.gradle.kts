plugins {
    id("net.rubygrapefruit.kmp.base-lib")
    id("net.rubygrapefruit.bootstrap.release")
    id("net.rubygrapefruit.bootstrap.docs")
}

group = versions.libs.group

library {
    jvm {
        module.name = "net.rubygrapefruit.cli_app"
        targetJvmVersion = 11
    }
    nativeDesktop()
    test {
        implementation(versions.test.coordinates)
        implementation(project(":file-fixtures"))
    }
    common {
        api(project(":file-io"))
        api(project(":cli-args"))
    }
}

component {
    description = "A small framework to help implement CLI applications using Kotlin multiplatform."
}
