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
package ac.simons.tdd.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The service is the central element for the application logic to interact with events and registrations. It represents
 * a transaction boundary.
 *
 * @author Michael J. Simons, 2017-10-31
 */
@Service
@Transactional
@RequiredArgsConstructor
// tag::eventServiceSaveEvents[]
public class EventService {
    private final EventRepository eventRepository;

    // end::eventServiceSaveEvents[]

    /**
     * @param heldOn
     * @param name
     * @return The event with the given date and name
     */
    public Optional<Event> getEvent(final LocalDate heldOn, final String name) {
        return this.eventRepository.findOne(new Event(heldOn, name).asExample());
    }

    /**
     * Creates a new event and checks first wether the event already exists or not.
     *
     * @param newEvent
     * @return
     */
    // tag::eventServiceSaveEvents[]
    public Event createNewEvent(final Event newEvent) {
        this.eventRepository.findOne(newEvent.asExample())
            .ifPresent(e -> {
                throw new DuplicateEventException(e);
            });
        return this.eventRepository.save(newEvent);
    }
    // end::eventServiceSaveEvents[]

    /**
     * Registers for a new event.
     *
     * @param event
     * @param person The person to register for the event
     * @return The confirmed registration
     */
    public Registration registerFor(final Event event, final Person person) {
        final Event persistentEvent =
              this.eventRepository.findOne(event.asExample()).orElseThrow(NoSuchEventException::new);
        return persistentEvent.register(person);
    }

    /**
     * @return a list of open events in the future
     */
    public List<Event> getOpenEvents() {
        return this.eventRepository.findAllOpenEvents();
    }
    // tag::eventServiceSaveEvents[]
}
// end::eventServiceSaveEvents[]
