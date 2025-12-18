package net.rubygrapefruit.plugins.stage1;

import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.PluginDeclaration;

import javax.inject.Inject;

public abstract class PluginBundle {
    private final GradlePluginDevelopmentExtension pluginDevExtension;

    @Inject
    public PluginBundle(GradlePluginDevelopmentExtension pluginDevExtension) {
        this.pluginDevExtension = pluginDevExtension;
    }

    public void plugin(String id, String implementation) {
        PluginDeclaration pluginDeclaration = pluginDevExtension.getPlugins().create(id);
        pluginDeclaration.setId(id);
        pluginDeclaration.setImplementationClass(implementation);
    }
}
