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
package ac.simons.simplemeetup.app;

import ac.simons.simplemeetup.domain.Event;
import ac.simons.simplemeetup.domain.Person;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * This module is used to extract all the Jackson stuff away from the domain.
 *
 * @author Michael J. Simons, 2017-12-20
 */
public final class EventsModule extends SimpleModule {

    /**
     * Registers all nested mixins, serializers and deserializers.
     */
    public EventsModule() {
        setMixInAnnotation(Event.class, EventMixIn.class);
        setMixInAnnotation(Person.class, PersonMixIn.class);
    }

    @JsonAutoDetect(fieldVisibility = NONE, getterVisibility = NONE, isGetterVisibility = NONE)
    abstract static class EventMixIn {
        @JsonCreator
        EventMixIn(@JsonProperty("heldOn") final LocalDate heldOn, @JsonProperty("name") final String name) {
        }

        @JsonProperty
        abstract LocalDate getHeldOn();

        @JsonProperty
        abstract String getName();

        @JsonProperty
        abstract Integer getNumberOfFreeSeats();
    }

    abstract static class PersonMixIn {

        @JsonCreator
        PersonMixIn(@JsonProperty("email") final String email, @JsonProperty("name") final String name) {
        }
    }
}
