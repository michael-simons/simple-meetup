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

import org.springframework.stereotype.Service;

/**
 * The service is the central element for the application logic to interact with events and registrations.
 *
 * @author Michael J. Simons, 2017-10-31
 */
@Service
public final class EventService {
    private final EventRepository eventRepository;

    public EventService(final EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createNewEvent(final Event newEvent) {
        this.eventRepository.findOneByHeldOn(newEvent.getHeldOn()).ifPresent(e -> {
            throw new DuplicateEventException(e);
        });
        return this.eventRepository.save(newEvent);
    }
}
