pluginManagement {
    includeBuild("../stage1")
    includeBuild("../stage2")
}
plugins {
    id("net.rubygrapefruit.stage2.settings")
    id("net.rubygrapefruit.stage2.included-build")
    id("net.rubygrapefruit.stage2.stage-dsl")
}

projects {
    downgrade("local-plugins/build-constants")
    downgrade("local-plugins/settings-plugins")
    downgrade("local-plugins/model")
    downgrade("local-plugins/release")
    downgrade("local-plugins/samples")
    downgrade("local-plugins/basics")
    downgrade("local-plugins/cpu-info")
    downgrade("local-plugins/machine-info")
    downgrade("local-plugins/bytecode")
    downgrade("local-plugins/base-plugins")
    downgrade("local-plugins/convention-plugins")
}
