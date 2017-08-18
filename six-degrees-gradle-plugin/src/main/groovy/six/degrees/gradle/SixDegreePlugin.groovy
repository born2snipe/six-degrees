package six.degrees.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class SixDegreePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.getPluginManager().apply(JavaPlugin.class);

        project.afterEvaluate {
            def generateSixDegreesFileTask = project.tasks.create("generateSixDegreesFile", GenerateSixDegreeFileTask)

            def processResourcesTask = project.tasks.findByName("processResources")
            processResourcesTask?.dependsOn generateSixDegreesFileTask
        }
    }
}
