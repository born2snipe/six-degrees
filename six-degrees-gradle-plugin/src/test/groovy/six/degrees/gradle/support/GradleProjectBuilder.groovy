package six.degrees.gradle.support

class GradleProjectBuilder {
    private String name
    private Map<String, List<String>> dependencies = [:]
    private List<String> pluginIds = []
    private List<String> directoriesPaths = []
    private Map<String, String> filePathsToContents = [:]

    public GradleProjectBuilder() {
        this("test")
    }

    public GradleProjectBuilder(String name) {
        this.name = name
    }

    GradleProjectBuilder addCompileDependency(String dependencyId) {
        addDependency("compile", dependencyId)
    }

    GradleProjectBuilder addDependency(String scope, String dependencyId) {
        if (!dependencies.containsKey(scope)) {
            dependencies[scope] = []
        }
        dependencies[scope] << dependencyId
        this
    }

    GradleProjectBuilder javaProject() {
        addPlugin('java')
        addDirectory('src/main/java')
        addDirectory('src/test/java')
    }

    GradleProjectBuilder groovyProject() {
        addPlugin("groovy")
        addDirectory('src/main/groovy')
        addDirectory('src/test/groovy')
        this
    }

    GradleProjectBuilder addDirectory(String directoryPath) {
        directoriesPaths << directoryPath
        this
    }

    GradleProjectBuilder addPlugin(String pluginId) {
        pluginIds << pluginId
        this
    }

    GradleProjectBuilder addFile(String filePath, String fileContents) {
        filePathsToContents[filePath] = fileContents
        this
    }

    void build(File rootDirectory) {
        assert rootDirectory.exists(), "${rootDirectory} does NOT exist"
        assert rootDirectory.isDirectory(), "${rootDirectory} is NOT a directory"

        setupProjectStructureIn(rootDirectory)
        setupGradleFile(rootDirectory)
        setupSettingsFile(rootDirectory)
    }

    private setupSettingsFile(File rootDirectory) {
        File settingsFile = new File(rootDirectory, "settings.gradle")
        settingsFile.text =
                """
rootProject.name = '${name}'

"""
    }

    private setupProjectStructureIn(File rootDirectory) {
        directoriesPaths.each { path ->
            File directory = new File(rootDirectory, path)
            if (!directory.exists()) {
                directory.mkdirs()
            }
        }

        filePathsToContents.each { path, contents ->
            File file = new File(rootDirectory, path)
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.text = contents
            }
        }
    }

    private setupGradleFile(File rootDirectory) {
        File gradleFile = new File(rootDirectory, "build.gradle")
        gradleFile.text =
                """
plugins {
    id "six-degrees"
}

repositories {
    mavenLocal()

    maven { url "https://repo.gradle.org/gradle/libs-releases-local/" }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
    maven { url "http://repo.maven.apache.org/maven2" }
}

${pluginIds.collect { "apply plugin: '${it}'" }.join("\n")}

dependencies {
${dependencies.collect { scope, dependencyIds -> dependencyIds.collect { "  ${scope} '${it}'" }.join("\n") }.join("\n")}
}


"""
    }
}
