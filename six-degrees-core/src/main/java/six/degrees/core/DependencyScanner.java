package six.degrees.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DependencyScanner {
    public Dependencies findAndMerge(File... filesInClassPath) {
        return findAndMerge(Arrays
                .stream(filesInClassPath)
                .map(File::getAbsolutePath)
                .collect(Collectors.toList()));
    }

    public Dependencies findAndMerge(Collection<String> filesInClassPath) {
        URLClassLoader classloader = createClassLoaderWith(filesInClassPath);
        Dependencies dependencies = new Dependencies();
        DependenciesFileHandler dependenciesFileHandler = new DependenciesFileHandler();

        try {
            Enumeration<URL> urlToFile = classloader.findResources("META-INF/six-degrees.json");
            while (urlToFile.hasMoreElements()) {
                URL url = urlToFile.nextElement();
                try (InputStream input = url.openStream()) {
                    dependencies.add(dependenciesFileHandler.read(input));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return dependencies;
    }

    private URLClassLoader createClassLoaderWith(Collection<String> filesInClassPath) {
        URL[] urls = convertFilePathsToUrls(filesInClassPath);
        return new URLClassLoader(urls, null);
    }

    private URL[] convertFilePathsToUrls(Collection<String> filesInClassPath) {
        return filesInClassPath.stream()
                .map(addUrlPrefix())
                .map(addEndingSlashToPathAsNeeded())
                .map(buildUrl())
                .toArray(URL[]::new);
    }

    private Function<String, URL> buildUrl() {
        return (s) -> {
            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Function<String, String> addUrlPrefix() {
        return (s) -> "file:///" + s;
    }

    private Function<String, String> addEndingSlashToPathAsNeeded() {
        return (s) -> {
            if (s.endsWith(".jar")) {
                return s;
            }
            return s + "/";
        };
    }
}
