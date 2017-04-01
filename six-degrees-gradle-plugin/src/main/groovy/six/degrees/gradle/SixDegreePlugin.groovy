package six.degrees.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project


class SixDegreePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.afterEvaluate {
            def generateSixDegreesFileTask = project.tasks.create("generateSixDegreesFile", GenerateSixDegreeFileTask)

            def processResourcesTask = project.tasks.findByName("processResources")
            processResourcesTask?.dependsOn generateSixDegreesFileTask


        }
    }
}
