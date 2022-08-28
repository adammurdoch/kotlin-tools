
project.tasks.register("clean") {
    dependsOn(subprojects.map { "${it.path}:clean" })
}

project.tasks.register("dist") {
    dependsOn(subprojects.map { "${it.path}:dist" })
}

project.tasks.register("build") {
    dependsOn(subprojects.map { "${it.path}:build" })
}
