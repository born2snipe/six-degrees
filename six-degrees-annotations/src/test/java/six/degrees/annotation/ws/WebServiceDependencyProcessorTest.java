/**
 *
 * Copyright to the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package six.degrees.annotation.ws;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import six.degrees.core.Dependencies;
import six.degrees.core.DependenciesFileRepository;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


public class WebServiceDependencyProcessorTest {
    @Test
    public void shouldBeAbleDeclareAWebServiceDependencyOnAType() {
        Compilation compilation = successfullyCompiles(
                webServiceDeclaredOnType("WebServiceOneRepository", "web-service-1")
        );

        assertWebServiceNames(compilation, asList("web-service-1"));
    }

    @Test
    public void shouldBeAbleDeclareAWebServiceDependencyOnAMethod() {
        Compilation compilation = successfullyCompiles(
                webServiceDeclaredOnMethod("WebServiceOneRepository", "web-service-1")
        );
        assertWebServiceNames(compilation, asList("web-service-1"));
    }

    @Test
    public void shouldAppendWebServiceDependenciesToTheExistingFile() {
        Compilation compilation = successfullyCompiles(
                webServiceDeclaredOnMethod("WebServiceOneRepository", "web-service-1"),
                webServiceDeclaredOnMethod("WebServiceTwoRepository", "web-service-2"),
                webServiceDeclaredOnType("WebServiceThreeRepository", "web-service-3")
        );

        assertWebServiceNames(compilation, asList("web-service-1", "web-service-2", "web-service-3"));
    }

    @Test
    public void shouldAllowMultipleWebServiceDependenciesOnASingleClass() {
        Compilation compilation = successfullyCompiles(
                webServiceDeclaredOnTypeAndMethod("WebServiceOneRepository", "web-service-1", "web-service-2"),
                webServiceDeclaredOnMultipleMethods("WebServiceTwoRepository", "web-service-3", "web-service-4")
        );

        assertWebServiceNames(compilation, asList("web-service-1", "web-service-2", "web-service-3", "web-service-4"));
    }

    @Ignore("not sure how to make this work... :(")
    @Test
    public void shouldNotAllowDeclaringWebServiceDependencyOnAMethodAndType() {
        Compilation compilation = javac()
                .withProcessors(new WebServiceDependencyProcessor())
                .compile(webServiceDeclaredOnTypeAndMethod("WebServiceOneRepository", "web-service-1", "web-service-1"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("You can NOT declare a @WebServiceDependency on the TYPE and a METHOD. Please choose one or the other.");
    }

    private Compilation successfullyCompiles(JavaFileObject... filesToCompile) {
        Compilation compilation = javac()
                .withProcessors(new WebServiceDependencyProcessor())
                .compile(filesToCompile);

        assertThat(compilation).succeededWithoutWarnings();
        assertThat(compilation).generatedFile(StandardLocation.CLASS_OUTPUT, "META-INF/six-degrees.json");
        return compilation;
    }

    private void assertWebServiceNames(Compilation compilation, List<String> expectedWebServiceNames) {
        Optional<JavaFileObject> file = compilation.generatedFile(StandardLocation.CLASS_OUTPUT, "META-INF/six-degrees.json");
        assertTrue("No file found @ META-INF/six-degrees.json", file.isPresent());
        try (InputStream input = file.get().openInputStream()) {
            Dependencies socials = new DependenciesFileRepository().read(input);
            assertEquals(new HashSet<>(expectedWebServiceNames), socials.getWebServiceDependencies());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JavaFileObject webServiceDeclaredOnTypeAndMethod(String className, String webServiceNameType, String webServiceNameMethod) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("ws/DeclaredOnTypeAndMethod.java")) {
            String javaSource = IOUtils.toString(input);
            javaSource = javaSource.replace("{ClassName}", className);
            javaSource = javaSource.replace("{WebServiceNameType}", webServiceNameType);
            javaSource = javaSource.replace("{WebServiceNameMethod}", webServiceNameMethod);
            return JavaFileObjects.forSourceString(className, javaSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JavaFileObject webServiceDeclaredOnMultipleMethods(String className, String webServiceNameMethod1, String webServiceNameMethod2) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("ws/DeclaredOnMultipleMethods.java")) {
            String javaSource = IOUtils.toString(input);
            javaSource = javaSource.replace("{ClassName}", className);
            javaSource = javaSource.replace("{WebServiceNameMethod1}", webServiceNameMethod1);
            javaSource = javaSource.replace("{WebServiceNameMethod2}", webServiceNameMethod2);
            return JavaFileObjects.forSourceString(className, javaSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JavaFileObject webServiceDeclaredOnType(String className, String webServiceName) {
        return newWebServiceClass(className, webServiceName, "ws/DeclaredOnType.java");
    }

    private JavaFileObject webServiceDeclaredOnMethod(String className, String webServiceName) {
        return newWebServiceClass(className, webServiceName, "ws/DeclaredOnMethod.java");
    }

    private JavaFileObject newWebServiceClass(String className, String webServiceName, String filename) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)) {
            String javaSource = IOUtils.toString(input);
            javaSource = javaSource.replace("{ClassName}", className);
            javaSource = javaSource.replace("{WebServiceName}", webServiceName);
            return JavaFileObjects.forSourceString(className, javaSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}