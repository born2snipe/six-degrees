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
package six.degrees.core.plantuml;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class PlantUmlDiagramGeneratorTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();
    private PlantUmlDiagramGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new PlantUmlDiagramGenerator();
    }

    @Test
    public void shouldBeAbleToGenerateAnImage() throws IOException {
        File file = tmp.newFile();

        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("example-plantuml.txt");
        generator.generate(input, new FileOutputStream(file));

        assertTrue(file.length() > 0);
    }
}