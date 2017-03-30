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
import org.junit.Test;
import six.degrees.core.Dependencies;

import static org.junit.Assert.assertEquals;

public class DependenciesToPlantUmlTranslatorTest {
    private ApplicationSocialToPlantUmlTranslator translator;

    @Before
    public void setUp() throws Exception {
        translator = new ApplicationSocialToPlantUmlTranslator();
    }

    @Test
    public void shouldHandleNoSocials() {
        assertEquals("", translator.translate("app", new Dependencies()));
    }

    @Test
    public void shouldHandleMultipleWebServiceDependencies() {
        Dependencies socials = new Dependencies();
        socials.addWebServiceDependency("dep-1");
        socials.addWebServiceDependency("dep-2");

        assertEquals("[app] --> [dep-1]\n[app] --> [dep-2]", translator.translate("app", socials));
    }
}