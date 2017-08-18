package six.degrees.gradle

import org.gradle.testkit.runner.BuildResult
import org.junit.Ignore
import org.junit.Test
import six.degrees.gradle.support.GradleProjectBuilder
import six.degrees.gradle.support.GradleTest

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS


class AnnotationProcessingTest extends GradleTest {
    @Ignore
    @Test
    public void x() {
        new GradleProjectBuilder("module-1")
                .javaProject()
                .addCompileDependency("com.github.born2snipe:six-degrees:4.12")
                .addCompileDependency("org.mockito:mockito-all:1.10.19")
                .addCompileDependency("org.springframework.boot:spring-boot:1.5.2.RELEASE")
                .addFile("java/main/TestClient.java", "")
                .build(tmp.root)

        BuildResult result = executeGradleTask("compileJava")
        assert result.task(":compileJava").getOutcome() == SUCCESS
    }

}
