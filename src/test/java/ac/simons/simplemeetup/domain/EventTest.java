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

import ac.simons.simplemeetup.domain.EventTest.Logic;
import ac.simons.simplemeetup.domain.EventTest.Postconditions;
import ac.simons.simplemeetup.domain.EventTest.Preconditions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static ac.simons.simplemeetup.domain.Events.alreadyRegisteredEvent;
import static ac.simons.simplemeetup.domain.Events.closedEvent;
import static ac.simons.simplemeetup.domain.Events.fullEvent;
import static ac.simons.simplemeetup.domain.Events.pastEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Michael J. Simons, 2017-10-31
 */
// tag::suiteExample[]
@RunWith(EventTest.class)
@SuiteClasses({Preconditions.class, Postconditions.class, Logic.class})
public class EventTest extends Suite {
    // end::suiteExample[]
    public EventTest(final Class<?> klass, final RunnerBuilder builder) throws InitializationError {
        super(klass, builder);
    }

    @BeforeClass
    public static void prepareEventClock() {
        Event.CLOCK.set(
            Clock.fixed(Instant.parse("2018-01-01T08:00:00.00Z"), ZoneId.systemDefault()));
    }

    // tag::event-entity-tests[]
    public static class Preconditions {
        // end::event-entity-tests[]
        @Test
        public void constructorShouldNotAllowInvalidDates() {
            Stream.of(LocalDate.of(2017, 10, 31), null).forEach(date ->
                assertThatThrownBy(() -> new Event(date, "test"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Event requires a date in the future.")
            );
        }

        // tag::event-entity-tests[]
        @Test // <1>
        public void constructorShouldNotAllowInvalidNames() {
            Stream.of(null, "", "\t", " ").forEach(name ->
                assertThatThrownBy(() -> new Event(LocalDate.of(2018, 1, 2), name))  // <2>
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Event requires a non-empty name.")  // <3>
            );
        }
    }
    // end::event-entity-tests[]

    // tag::event-entity-tests[]
    public static class Postconditions {
        @Test
        public void constructorShouldCreateValidEvents() {
            final Integer numberOfSeats = 23;
            final LocalDate heldOn = LocalDate.of(2018, 1, 2);
            final Event event = new Event(
                heldOn, "test", numberOfSeats);
            // end::event-entity-tests[]
            assertThat(event.getHeldOn()).isEqualTo(heldOn);
            assertThat(event.getName()).isEqualTo("test");
            // tag::event-entity-tests[]
            assertThat(event.getNumberOfSeats()).isEqualTo(numberOfSeats);
            assertThat(event.isOpen()).isTrue();
        }
    }
    // end::event-entity-tests[]

    public static class Logic {
        @Test
        public void registrationShouldWork() {
            final Map<Event, Class<? extends Exception>> events = new HashMap<>();
            events.put(closedEvent(), IllegalStateException.class);
            events.put(pastEvent(), IllegalStateException.class);
            events.put(fullEvent(), IllegalStateException.class);
            events.put(alreadyRegisteredEvent(), IllegalArgumentException.class);

            events.entrySet().stream().map(entry ->
                assertThatThrownBy(() -> entry.getKey().register(new Person("test@test.com", "test")))
                    .isInstanceOf(entry.getValue())
                    .withFailMessage("Should not be able to register to event with wrong state")
            );
        }
    }

    @AfterClass
    public static void resetEventClock() {
        Event.CLOCK.set(Clock.systemDefaultZone());
    }

    // tag::suiteExample[]
}
// end::suiteExample[]
