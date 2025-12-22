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
    upgrade("stage2/plugins")
    upgrade("stage2/settings-plugins", "stage2-settings-plugins")
    downgrade("local-plugins/build-constants")
    downgrade("local-plugins/settings-plugins")
    downgrade("local-plugins/model")
    downgrade("local-plugins/release")
    downgrade("local-plugins/samples")
}
