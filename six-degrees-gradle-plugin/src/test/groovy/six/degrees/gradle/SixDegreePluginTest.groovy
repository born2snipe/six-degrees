package six.degrees.gradle

import org.gradle.testkit.runner.BuildResult
import org.junit.Test
import six.degrees.gradle.support.GradleProjectBuilder
import six.degrees.gradle.support.GradleTest

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SixDegreePluginTest extends GradleTest {
    @Test
    void x() {
        new GradleProjectBuilder()
                .javaProject()
                .addCompileDependency("junit:junit:4.12")
                .addCompileDependency("org.mockito:mockito-all:1.10.19")
                .build(tmp.root)

        BuildResult result = executeGradleTask("build")
        assert result.task(":generateSixDegreesFile").getOutcome() == SUCCESS
    }

    @Test
    void the_generate_six_degree_file_task_should_run_when_processResources_is_executed_groovy_project() {
        new GradleProjectBuilder()
                .groovyProject()
                .build(tmp.root)

        BuildResult result = executeGradleTask("processResources")
        assert result.task(":generateSixDegreesFile").getOutcome() == SUCCESS
    }

    @Test
    void the_generate_six_degree_file_task_should_run_when_processResources_is_executed_java_project() {
        new GradleProjectBuilder()
                .javaProject()
                .build(tmp.root)

        BuildResult result = executeGradleTask("processResources")
        assert result.task(":generateSixDegreesFile").getOutcome() == SUCCESS
    }

    @Test
    void should_have_the_generate_six_degree_file_task_by_default() {
        new GradleProjectBuilder()
                .build(tmp.root)

        BuildResult result = executeGradleTask("generateSixDegreesFile")
        assert result.output.contains("generateSixDegreesFile")
        assert result.task(":generateSixDegreesFile").getOutcome() == SUCCESS
    }

}
