package six.degrees.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import six.degrees.core.Dependencies
import six.degrees.core.DependenciesFileWriter


class GenerateSixDegreeFileTask extends DefaultTask {
    @Input
    def buildFile = project.buildFile

    @OutputFile
    def sixDegreeFile = new File(project.buildDir, "resources/META-INF/six-degrees.json")

    @TaskAction
    void generateSixDegreeFile() {
        def writer = new DependenciesFileWriter()
        writer.write(new Dependencies(), sixDegreeFile)
    }
}
