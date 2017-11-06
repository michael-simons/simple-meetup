/*
 * Copyright 2017 michael-simons.eu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ac.simons.tdd.support;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.Container;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.io.IOException;

/**
 * @author Michael J. Simons, 2017-11-06
 */
public final class PortMappingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static final ThreadLocal<DockerComposeRule> DOCKER =
        ThreadLocal.withInitial(() -> null);

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        final DockerComposeRule docker = DOCKER.get();

        if (docker != null) {
            try {
                final ConfigurableEnvironment environment = applicationContext.getEnvironment();
                for (Container container : docker.containers().allContainers()) {
                    container.ports().stream().forEach(p -> {
                        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(environment,
                            container.getContainerName() + ".port = " + p.getExternalPort());
                    });
                }
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
