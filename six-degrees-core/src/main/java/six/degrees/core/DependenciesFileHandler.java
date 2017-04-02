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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DependenciesFileHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    public Dependencies read(File file) {
        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            return read(input);
        } catch (IOException e) {
            throw new RuntimeException("A problem occurred trying to read the file: " + file, e);
        }
    }

    public Dependencies read(InputStream input) {
        try {
            return objectMapper.readValue(input, Dependencies.class);
        } catch (IOException e) {
            throw new RuntimeException("A problem occurred trying to read the file", e);
        }
    }

    public void write(Dependencies dependencies, File outputFile) {
        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            write(dependencies, output);
        } catch (IOException e) {
            throw new RuntimeException("A problem occurred trying to write the file: " + outputFile, e);
        }
    }

    public void write(Dependencies dependencies, OutputStream outputFile) {
        try {
            objectMapper.writeValue(outputFile, dependencies);
        } catch (IOException e) {
            throw new RuntimeException("A problem occurred trying to write the file", e);
        }
    }
}
