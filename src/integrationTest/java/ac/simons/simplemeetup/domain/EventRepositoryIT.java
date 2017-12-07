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
package ac.simons.simplemeetup.domain;

import ac.simons.simplemeetup.support.PortMappingInitializer;
import ac.simons.simplemeetup.support.PostgresHealthChecks;
import ac.simons.simplemeetup.support.PropagateDockerRule;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael J. Simons, 2017-11-05
 */
// tag::event-repository-custom-query-test[]
@RunWith(SpringRunner.class)
// end::event-repository-custom-query-test[]
@ActiveProfiles("it")
// tag::event-repository-custom-query-test[]
@DataJpaTest
@ContextConfiguration(initializers = PortMappingInitializer.class)
public class EventRepositoryIT {

    private static DockerComposeRule docker = DockerComposeRule.builder()
        .file("src/integrationTest/resources/docker-compose.yml")
        // end::event-repository-custom-query-test[]
        .waitingForService("it-database", HealthChecks.toHaveAllPortsOpen())
        // tag::event-repository-custom-query-test[]
        .waitingForService("it-database", PostgresHealthChecks::canConnectTo)
        .build();

    @ClassRule
    public static TestRule exposePortMappings = RuleChain.outerRule(docker)
        .around(new PropagateDockerRule(docker));

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void someTest() {
        final List<Event> openEvents =
            this.eventRepository.findAllOpenEvents();

        final Event expectedEvent
            = new Event(LocalDate.now().plusDays(1), "Open Event");
        assertThat(openEvents)
            .containsExactly(expectedEvent)
            .extracting(Event::getNumberOfFreeSeats)
            .first()
            .isEqualTo(19);
    }
}
// end::event-repository-custom-query-test[]
