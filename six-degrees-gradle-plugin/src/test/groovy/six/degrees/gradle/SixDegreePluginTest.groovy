package six.degrees.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SixDegreePluginTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();


    @Test
    void the_generate_six_degree_file_task_should_run_when_processResources_is_executed_groovy_project() {
        File buildFile = tmp.newFile("build.gradle")
        buildFile.text = """
            plugins {
               id "six-degrees"
            }

            apply plugin: 'groovy'
        """

        BuildResult result = executeGradleTask("processResources")
        assert result.task(":generateSixDegreesFile").getOutcome() == SUCCESS
    }

    @Test
    void the_generate_six_degree_file_task_should_run_when_processResources_is_executed_java_project() {
        File buildFile = tmp.newFile("build.gradle")
        buildFile.text = """
            plugins {
               id "six-degrees"
            }

            apply plugin: 'java'
        """

        BuildResult result = executeGradleTask("processResources")
        assert result.task(":generateSixDegreesFile").getOutcome() == SUCCESS
    }

    @Test
    void should_have_the_generate_six_degree_file_task_by_default() {
        File buildFile = tmp.newFile("build.gradle")
        buildFile.text = """
            plugins {
               id "six-degrees"
            }
        """

        BuildResult result = executeGradleTask("generateSixDegreesFile")
        assert result.output.contains("generateSixDegreesFile")
        assert result.task(":generateSixDegreesFile").getOutcome() == SUCCESS
    }

    private BuildResult executeGradleTask(String... args) {
        GradleRunner.create()
                .withDebug(true)
                .withProjectDir(tmp.getRoot())
                .withPluginClasspath(PluginClasspath.get())
                .withArguments(args).build()
    }
}
