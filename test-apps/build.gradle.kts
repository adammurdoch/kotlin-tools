// TODO - need this for some reason
repositories.mavenCentral()

project.tasks.register("clean") {
    dependsOn(subprojects.map { "${it.path}:clean" })
}

project.tasks.register("install") {
    dependsOn(subprojects.map { "${it.path}:install" })
}

project.tasks.register("build") {
    dependsOn(subprojects.map { "${it.path}:build" })
}
