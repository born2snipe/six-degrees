package six.degrees.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class GenerateSixDegreeFileTaskTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder()

    @Test
    void should_consider_the_project_UP_TO_DATE_when_the_gradle_file_has_not_been_altered() {
        File buildFile = tmp.newFile("build.gradle")
        buildFile.text = """
            plugins {
               id "six-degrees"
            }
        """

        assert executeGradleTask("generateSixDegreesFile").task(":generateSixDegreesFile").getOutcome() == SUCCESS
        assert executeGradleTask("generateSixDegreesFile").task(":generateSixDegreesFile").getOutcome() == UP_TO_DATE
    }

    private BuildResult executeGradleTask(String... args) {
        GradleRunner.create()
                .withDebug(true)
                .withProjectDir(tmp.getRoot())
                .withPluginClasspath(PluginClasspath.get())
                .withArguments(args).build()
    }

}