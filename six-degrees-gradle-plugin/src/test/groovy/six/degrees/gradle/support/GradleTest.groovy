package six.degrees.gradle.support

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder

abstract class GradleTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder()


    BuildResult executeGradleTask(String... args) {
        List<String> gradleArgs = []
        gradleArgs.addAll(Arrays.asList(args))
//        gradleArgs.add("--debug")
        gradleArgs.add("--stacktrace")

        BuildResult result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(tmp.getRoot())
                .withPluginClasspath(PluginClasspath.get())
                .withArguments(gradleArgs)
                .build()

        println result.output

        result
    }

}
