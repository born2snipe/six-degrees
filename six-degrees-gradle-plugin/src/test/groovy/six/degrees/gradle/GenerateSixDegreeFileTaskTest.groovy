package six.degrees.gradle

import org.junit.Test
import six.degrees.gradle.support.GradleProjectBuilder
import six.degrees.gradle.support.GradleTest

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class GenerateSixDegreeFileTaskTest extends GradleTest {
    @Test
    void should_consider_the_project_UP_TO_DATE_when_the_gradle_file_has_not_been_altered() {
        new GradleProjectBuilder()
                .javaProject()
                .addCompileDependency("org.kohsuke.metainf-services:metainf-services:1.1")
                .build(tmp.root)

        assert executeGradleTask("generateSixDegreesFile").task(":generateSixDegreesFile").getOutcome() == SUCCESS
        assert executeGradleTask("generateSixDegreesFile").task(":generateSixDegreesFile").getOutcome() == UP_TO_DATE
    }
}