import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.gradle-plugin")
}

group = Versions.plugins.group

dependencies {
    implementation("org.commonmark:commonmark:0.22.0")
}

gradlePlugin {
    plugins {
        create("docs") {
            id = "net.rubygrapefruit.docs"
            implementationClass = "net.rubygrapefruit.plugins.docs.internal.DocsPlugin"
        }
    }
}
