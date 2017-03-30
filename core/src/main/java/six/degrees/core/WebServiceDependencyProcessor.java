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
package six.degrees.core;

import org.kohsuke.MetaInfServices;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@MetaInfServices(Processor.class)
public class WebServiceDependencyProcessor extends AbstractProcessor {
    private static final String OUTPUT_FILE = "META-INF/six-degrees.json";
    private DependenciesFileRepository fileRepository = new DependenciesFileRepository();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(WebServiceDependency.class);

        Collection<String> webServiceNames = collectWebServiceNamesFrom(elements);

        write(webServiceNames);

        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        annotationTypes.add(WebServiceDependency.class.getName());
        return annotationTypes;
    }

    private void write(Collection<String> entries) {
        try {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE);
            Dependencies socials = new Dependencies();
            for (String entry : entries) {
                socials.addWebServiceDependency(entry);
            }
            fileRepository.save(socials, file.openOutputStream());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
        }
    }

    private Collection<String> collectWebServiceNamesFrom(Set<? extends Element> elements) {
        return elements
                .stream()
                .map((e) -> e.getAnnotation(WebServiceDependency.class))
                .map(WebServiceDependency::value)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
    }
}
