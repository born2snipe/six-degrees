package six.degrees.core;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import zipunit.ZipBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DependencyScannerTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();
    private DependencyScanner scanner;

    @Before
    public void setUp() throws Exception {
        scanner = new DependencyScanner();
    }

    @Test
    public void shouldHandleADirectoryContainingTheDependencyFileAndJar() throws IOException {
        File classes = tmp.newFolder("classes");
        File json = new File(classes, "META-INF/six-degrees.json");
        json.getParentFile().mkdirs();
        IOUtils.write(withWebServiceDependencies("app-1", "app-2"), new FileOutputStream(json));

        File jarFile = new ZipBuilder(tmp.getRoot())
                .withEntry("META-INF/six-degrees.json", withWebServiceDependencies("app-3", "app-4"))
                .build("test.jar");

        Dependencies dependencies = scanner.findAndMerge(classes, jarFile);

        assertEquals(new HashSet<>(asList("app-1", "app-2", "app-3", "app-4")), dependencies.getWebServiceDependencies());
    }

    @Test
    public void shouldHandleADirectoryContainingTheDependencyFile() throws IOException {
        File metaInf = tmp.newFolder("META-INF");
        File json = new File(metaInf, "six-degrees.json");
        IOUtils.write(withWebServiceDependencies("app-1", "app-2"), new FileOutputStream(json));

        Dependencies dependencies = scanner.findAndMerge(tmp.getRoot());

        assertEquals(new HashSet<>(asList("app-1", "app-2")), dependencies.getWebServiceDependencies());
    }

    @Test
    public void shouldHandleWhenMultipleJarsContainADependencyFile() throws IOException {
        File jarFile = new ZipBuilder(tmp.getRoot())
                .withEntry("META-INF/six-degrees.json", withWebServiceDependencies("app-1", "app-2"))
                .build("test.jar");

        File otherJarFile = new ZipBuilder(tmp.getRoot())
                .withEntry("META-INF/six-degrees.json", withWebServiceDependencies("app-4", "app-3"))
                .build("other-test.jar");

        Dependencies dependencies = scanner.findAndMerge(jarFile, otherJarFile);

        assertEquals(new HashSet<>(asList("app-1", "app-2", "app-3", "app-4")), dependencies.getWebServiceDependencies());
    }

    @Test
    public void shouldHandleWhenOneDependencyFileIsFoundInAJar() throws IOException {
        File jarFile = new ZipBuilder(tmp.getRoot())
                .withEntry("META-INF/six-degrees.json", withWebServiceDependencies("app-1", "app-2"))
                .build("test.jar");

        Dependencies dependencies = scanner.findAndMerge(jarFile);

        assertEquals(new HashSet<>(asList("app-1", "app-2")), dependencies.getWebServiceDependencies());
    }

    private String withWebServiceDependencies(String... apps) throws IOException {
        String appsJson = Arrays.stream(apps)
                .map((s) -> "\"" + s + "\"")
                .collect(Collectors.joining(", "));

        return "{\"webServiceDependencies\": [ " + appsJson + " ] }";
    }

    @Test
    public void shouldHandleWhenNothingIsInTheClassPath() {
        Dependencies dependencies = scanner.findAndMerge();

        assertTrue(dependencies.getWebServiceDependencies().isEmpty());
    }
}