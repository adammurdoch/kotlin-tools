import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
    id("net.rubygrapefruit.bootstrap.release")
}

group = Versions.libs.group

library {
    jvm {
        module.name = "net.rubygrapefruit.cli_args"
        targetJavaVersion = 11
    }
    nativeDesktop()
    test {
        implementation(Versions.test.coordinates)
    }
}

release {
    description = "A lightweight CLI argument parser for Kotlin multiplatform."
}
