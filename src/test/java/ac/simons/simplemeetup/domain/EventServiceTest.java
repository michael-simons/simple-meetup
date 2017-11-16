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

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static ac.simons.simplemeetup.domain.Events.halloween;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Michael J. Simons, 2017-11-01
 */
// tag::event-repository-usage-test[]
@RunWith(MockitoJUnitRunner.class) // <1>
public class EventServiceTest {
    // end::event-repository-usage-test[]

    public static final LocalDate NOVEMBER_1_ST = LocalDate.of(2018, 11, 1);

    @BeforeClass
    public static void prepareEventClock() {
        Event.CLOCK.set(
            Clock.fixed(Instant.parse("2018-01-01T08:00:00.00Z"), ZoneId.systemDefault()));
    }

    // tag::event-repository-usage-test[]
    @Mock // <2>
    private EventRepository eventRepository;

    @Test
    public void shouldNotCreateDuplicateEvents() {
        when(eventRepository.findOne(halloween().asExample()))
            .thenReturn(Optional.of(halloween())); // <3>

        final EventService eventService = new EventService(this.eventRepository);

        assertThatThrownBy(() -> eventService.createNewEvent(halloween()))
            .isInstanceOf(DuplicateEventException.class);

        verify(eventRepository, times(1))
                .findOne(halloween().asExample()); // <4>
    }
    // end::event-repository-usage-test[]

    @Test
    public void shouldCreateEvents() {
        when(eventRepository.findOne(new Event(NOVEMBER_1_ST, "test").asExample())).thenReturn(Optional.empty());
        when(eventRepository.save(any(Event.class))).then(returnsFirstArg());

        final EventService eventService = new EventService(this.eventRepository);

        final Event test = new Event(NOVEMBER_1_ST, "test");
        final Event newEvent = eventService.createNewEvent(test);

        assertThat(newEvent).isEqualTo(test);
    }

    // tag::event-repository-usage-test[]
}
// end::event-repository-usage-test[]
