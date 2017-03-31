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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class DependenciesFileRepositoryTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();
    private DependenciesFileRepository repository;


    @Before
    public void setUp() throws Exception {
        repository = new DependenciesFileRepository();
    }

    @Test
    public void shouldBeAbleToWriteAnEmptyFile() throws IOException {
        File file = tmp.newFile();

        repository.save(new Dependencies(), file);

        JsonNode jsonNode = new ObjectMapper().readTree(file);
        JsonNode webServicesDepsNode = jsonNode.get("webServiceDependencies");
        assertEquals(0, webServicesDepsNode.size());
        assertEquals(1, jsonNode.size());
    }

    @Test
    public void shouldBeAbleToWriteAFileWithWebServiceDeps() throws IOException {
        File file = tmp.newFile();

        Dependencies socials = new Dependencies();
        socials.addWebServiceDependency("app-1");
        socials.addWebServiceDependency("app-2");

        repository.save(socials, file);

        JsonNode jsonNode = new ObjectMapper().readTree(file);
        JsonNode webServicesDepsNode = jsonNode.get("webServiceDependencies");
        assertEquals("app-1", webServicesDepsNode.get(0).textValue());
        assertEquals("app-2", webServicesDepsNode.get(1).textValue());
        assertEquals(2, webServicesDepsNode.size());
        assertEquals(1, jsonNode.size());
    }

    @Test
    public void shouldBeAbleToReadAFileWithWebServiceDependencies() throws IOException {
        File file = copyFile("social/example-six-degrees.json");

        Dependencies socials = repository.read(file);

        assertEquals(new HashSet<>(asList("app-1", "app-2")), socials.getWebServiceDependencies());
    }

    @Test
    public void shouldNotCareAboutDuplicateWebServiceDependencies() throws IOException {
        File file = copyFile("social/example-six-degrees-with-duplicate-web-services.json");

        Dependencies socials = repository.read(file);

        assertEquals(new HashSet<>(asList("app-1")), socials.getWebServiceDependencies());
    }

    private File copyFile(String filename) throws IOException {
        File file = tmp.newFile();
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename); OutputStream output = new FileOutputStream(file)) {
            IOUtils.copy(input, output);
        }
        return file;
    }
}