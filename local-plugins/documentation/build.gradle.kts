import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
}

gradlePlugin {
    plugins {
        create("docs") {
            id = "net.rubygrapefruit.bootstrap.docs"
            implementationClass = "net.rubygrapefruit.plugins.docs.internal.DocsPlugin"
        }
    }
}

dependencies {
    implementation("org.commonmark:commonmark:0.22.0")
    implementation(project(":model"))
}

