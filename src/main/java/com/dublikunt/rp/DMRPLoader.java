package com.dublikunt.rp;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jspecify.annotations.NonNull;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class DMRPLoader implements PluginLoader {

    @Override
    public void classloader(@NonNull PluginClasspathBuilder pluginClasspath) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        Yaml yaml = new Yaml();
        try (InputStream inputStream = DMRPLoader.class
                .getClassLoader()
                .getResourceAsStream("paper-libraries.yml")) {

            if (inputStream == null) {
                System.err.println("paper-libraries.yml not found in the classpath.");
                return;
            }

            Map<String, List<String>> data = yaml.load(inputStream);
            List<String> libraries = data.get("libraries");

            if (libraries != null) {
                libraries.stream()
                        .map(DefaultArtifact::new)
                        .forEach(artifact ->
                                resolver.addDependency(new Dependency(artifact, null))
                        );
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load paper-libraries.yml", e);
        }

        resolver.addRepository(
                new RemoteRepository.Builder(
                        "paper",
                        "default",
                        "https://repo.papermc.io/repository/maven-public/"
                ).build()
        );

        pluginClasspath.addLibrary(resolver);
    }
}
