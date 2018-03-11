package com.github.schmittjoaopedro.analyser.spotbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.config.UserPreferences;
import edu.umd.cs.findbugs.plugins.DuplicatePluginIdException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

@ParametersAreNonnullByDefault
public class AnalysisRunnerSource {
    private final List<Path> auxClasspathEntries = new ArrayList<>();

    @Nullable
    private static final File PLUGIN_JAR;

    static {
        File jarFile;
        try {
            jarFile = createTempJar();
            Plugin.loadCustomPlugin(jarFile, null);
        } catch (DuplicatePluginIdException ignore) {
            // loading core plugin
            jarFile = null;
        } catch (IOException | URISyntaxException | PluginException e) {
            throw new AssertionError(e);
        }

        PLUGIN_JAR = jarFile;
    }

    @Nonnull
    public BugCollectionBugReporter run(Path... files) {
        DetectorFactoryCollection.resetInstance(new DetectorFactoryCollection());

        FindBugs2 engine = new FindBugs2();
        final Project project = new Project();
        project.setProjectName(getClass().getSimpleName());
        engine.setProject(project);

        if (PLUGIN_JAR != null) {
            try {
                String pluginId = Plugin.addCustomPlugin(PLUGIN_JAR.toURI()).getPluginId();
                project.setPluginStatusTrinary(pluginId, Boolean.TRUE);
            } catch (PluginException e) {
                throw new AssertionError("Failed to load plugin", e);
            }
        }
        final DetectorFactoryCollection detectorFactoryCollection = DetectorFactoryCollection.instance();
        engine.setDetectorFactoryCollection(detectorFactoryCollection);

        BugCollectionBugReporter bugReporter = new BugCollectionBugReporter(project);
        bugReporter.setPriorityThreshold(Priorities.LOW_PRIORITY);
        bugReporter.setRankThreshold(BugRanker.VISIBLE_RANK_MAX);

        engine.setBugReporter(bugReporter);
        final UserPreferences preferences = UserPreferences.createDefaultUserPreferences();
        preferences.getFilterSettings().clearAllCategories();
        preferences.enableAllDetectors(true);
        engine.setUserPreferences(preferences);

        for (Path file : files) {
            project.addFile(file.toAbsolutePath().toString());
        }
        for (Path auxClasspathEntry : auxClasspathEntries) {
            project.addAuxClasspathEntry(auxClasspathEntry.toAbsolutePath().toString());
        }

        try {
            engine.execute();
        } catch (final IOException | InterruptedException e) {
            throw new AssertionError("Analysis failed with exception", e);
        }
        return bugReporter;
    }

    private static File createTempJar() throws IOException, URISyntaxException {
        ClassLoader cl = AnalysisRunnerSource.class.getClassLoader();

        URL resource = cl.getResource("findbugs.xml");
        URI uri = resource.toURI();

        if ("jar".equals(uri.getScheme())) {
            JarURLConnection connection = (JarURLConnection) resource.openConnection();
            URL url = connection.getJarFileURL();
            return new File(url.getFile());
        }

        Path tempJar = File.createTempFile("SpotBugsAnalysisRunner", ".jar").toPath();
        try (OutputStream output = Files.newOutputStream(tempJar, StandardOpenOption.WRITE);
             JarOutputStream jar = new JarOutputStream(output)) {
            Path resourceRoot = Paths.get(uri).getParent();

            byte[] data = new byte[4 * 1024];
            Files.walkFileTree(resourceRoot, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String name = resourceRoot.relativize(file).toString();
                    jar.putNextEntry(new ZipEntry(name));
                    try (InputStream input = Files.newInputStream(file, StandardOpenOption.READ)) {
                        int len;
                        while ((len = input.read(data)) > 0) {
                            jar.write(data, 0, len);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return tempJar.toFile();
    }
}
