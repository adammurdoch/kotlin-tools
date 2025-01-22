import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
    id("net.rubygrapefruit.bootstrap.release")
    id("net.rubygrapefruit.bootstrap.docs")
}

group = Versions.libs.group

library {
    jvm {
        module.name = "net.rubygrapefruit.cli_app"
        targetJavaVersion = 11
    }
    nativeDesktop()
    test {
        implementation(Versions.test.coordinates)
        implementation(project(":file-fixtures"))
    }
    common {
        api(project(":file-io"))
        api(project(":cli-args"))
    }
}

release {
    description = "A small framework to help implement CLI applications using Kotlin multiplatform."
}
