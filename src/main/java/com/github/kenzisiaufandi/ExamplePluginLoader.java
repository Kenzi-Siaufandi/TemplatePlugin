package com.github.kenzisiaufandi;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public final class ExamplePluginLoader implements PluginLoader {
    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver lamp = new MavenLibraryResolver();

        lamp.addDependency(new Dependency(new DefaultArtifact("io.github.revxrsal:lamp.common:4.0.0"), null));
        lamp.addDependency(new Dependency(new DefaultArtifact("io.github.revxrsal:lamp.bukkit:4.0.0"), null));

        lamp.addRepository(new RemoteRepository.Builder(
                "central",
                "default",
                MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
        )
            .build()
        );

        classpathBuilder.addLibrary(lamp);
    }
}
