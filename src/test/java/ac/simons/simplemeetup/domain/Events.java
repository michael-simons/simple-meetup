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

import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

/**
 * @author Michael J. Simons, 2017-11-01
 */
final class Events {
    private Events() {
    }

    static Event halloween() {
        return new Event(LocalDate.of(2018, 10, 31), "Halloween");
    }

    static Event closedEvent() {
        final Event event = new Event(LocalDate.of(2018, 1, 2), "closedEvent");
        event.close();
        return event;
    }

    static Event pastEvent() {
        final Event event = new Event(LocalDate.of(2018, 1, 2), "pastEvent");
        ReflectionTestUtils.setField(event, "heldOn", LocalDate.of(2017, 1, 2));
        return event;
    }

    static Event fullEvent() {
        final Event event = new Event(LocalDate.of(2018, 1, 2), "fullEvent");
        ReflectionTestUtils.setField(event, "numberOfSeats", 0);
        return event;
    }

    static Event alreadyRegisteredEvent() {
        final Event event = new Event(LocalDate.of(2018, 1, 2), "alreadyRegisteredEvent");
        event.register(new Person("test@test.com", "test"));
        return event;
    }
}
