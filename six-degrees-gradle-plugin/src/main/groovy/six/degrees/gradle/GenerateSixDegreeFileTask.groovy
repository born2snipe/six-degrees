package six.degrees.gradle

import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import six.degrees.core.Dependencies
import six.degrees.core.DependenciesFileHandler
import six.degrees.core.DependencyScanner

class GenerateSixDegreeFileTask extends ConventionTask {
    @Input
    def buildFile = project.buildFile

    @OutputFile
    def sixDegreeFile = new File(project.buildDir, "resources/META-INF/six-degrees.json")

    @TaskAction
    void generateSixDegreeFile() {
        def configurationsToLookForFiles = ['runtimeClasspath']

        def configurationsToScan = project.configurations.findAll { configurationsToLookForFiles.contains(it.name) }
        List<String> jarFiles = new ArrayList<>()

        configurationsToScan.each { configuration ->
            def files = configuration.resolvedConfiguration.resolvedArtifacts.file*.canonicalPath

            if (logger.isDebugEnabled()) {
                logger.debug("Configuration: ${configuration.name}")
                files.each {
                    logger.debug("\t${it}")
                }
                logger.debug("")
            }

            jarFiles.addAll(files)
        }

        def dependencyScanner = new DependencyScanner();
        def writer = new DependenciesFileHandler()

        Dependencies dependencies = dependencyScanner.findAndMerge(jarFiles)
        writer.write(dependencies, sixDegreeFile)
    }
}
