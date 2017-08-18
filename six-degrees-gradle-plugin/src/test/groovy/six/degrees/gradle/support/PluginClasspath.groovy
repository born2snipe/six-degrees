package six.degrees.gradle.support;

public class PluginClasspath {
    static Collection<File> get() {
        def pluginClasspathResource = Thread.currentThread().contextClassLoader.findResource("plugin-classpath.txt")
        assert pluginClasspathResource, "'plugin-classpath.txt' was NOT found!!!"
        pluginClasspathResource.readLines().collect { new File(it) }
    }
}
