import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = Versions.libs.group

library {
    jvm {
        module.name = "net.rubygrapefruit.cli-app"
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