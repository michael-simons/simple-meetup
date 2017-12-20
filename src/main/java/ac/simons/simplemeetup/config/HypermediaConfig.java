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
package ac.simons.simplemeetup.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * This configures Hal+Forms for Spring HATEOAS. It's basically a copy of Greg Turnquists code from the officially
 * example repository one can find
 * <a href="https://github.com/spring-projects/spring-hateoas-examples/blob/master/affordances/src/main/java/org/springframework/hateoas/examples/HypermediaConfiguration.java">here</a>.
 *
 * @author Michael J. Simons, 2017-12-08
 */
@Profile("experimental")
@Configuration
@EnableHypermediaSupport(type = HypermediaType.HAL_FORMS)
public class HypermediaConfig {
    @Bean
    public static HalObjectMapperConfigurer halObjectMapperConfigurer(final BeanFactory beanFactory) {
        return new HalObjectMapperConfigurer(beanFactory);
    }

    private static class HalObjectMapperConfigurer implements BeanPostProcessor {

        private final BeanFactory beanFactory;

        HalObjectMapperConfigurer(final BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        /**
         * Assume any {@link ObjectMapper} starts with {@literal _hal} and ends with {@literal Mapper}.
         */
        @Override
        public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
            if (bean instanceof ObjectMapper && beanName.startsWith("_hal") && beanName.endsWith("Mapper")) {
                postProcessHalObjectMapper((ObjectMapper) bean);
            }
            return bean;
        }

        private void postProcessHalObjectMapper(final ObjectMapper objectMapper) {
            try {
                final Jackson2ObjectMapperBuilder builder = this.beanFactory.getBean(Jackson2ObjectMapperBuilder.class);
                builder.configure(objectMapper);
            } catch (NoSuchBeanDefinitionException ex) {
            }
        }
    }
}
