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
import ac.simons.simplemeetup.domain.Registration;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.io.IOException;
import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * This module is used to extract all the Jackson stuff away from the domain.
 *
 * @author Michael J. Simons, 2017-12-20
 */
// tag::json-module-example[]
public final class EventsModule extends SimpleModule {
    // end::json-module-example[]

    /**
     * Registers all nested mixins, serializers and deserializers.
     */
    // tag::json-module-example[]
    public EventsModule() {
        // end::json-module-example[]
        setMixInAnnotation(Event.class, EventMixIn.class);
        setMixInAnnotation(Person.class, PersonMixIn.class);
        // tag::json-module-example[]
        addSerializer(Registration.class, new RegistrationSerializer());
    }
    // end::json-module-example[]

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

    // tag::json-serializer-contract[]
    static class UnwrappingRegistrationSerializer extends JsonSerializer<Registration> {
        // end::json-serializer-contract[]

        private final NameTransformer nameTransformer;

        UnwrappingRegistrationSerializer(final NameTransformer nameTransformer) {
            this.nameTransformer = nameTransformer;
        }

        String hideEmail(final String email) {
            return email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
        }

        // tag::json-serializer-contract-unwrapping[]
        @Override
        public boolean isUnwrappingSerializer() {
            return true;
        }
        // end::json-serializer-contract-unwrapping[]

        // tag::json-serializer-contract[]
        @Override
        public void serialize(
            final Registration value,
            final JsonGenerator gen,
            final SerializerProvider serializers
        ) throws IOException {
            gen.writeStringField(nameTransformer.transform("name"), value.getName());
            gen.writeStringField(nameTransformer.transform("email"), hideEmail(value.getEmail()));
        }
    }
    // end::json-serializer-contract[]

    // tag::delegating-serializer[]
    static class RegistrationSerializer extends JsonSerializer<Registration> {

        private final JsonSerializer<Registration> delegate
            = new UnwrappingRegistrationSerializer(NameTransformer.NOP);

        @Override
        public void serialize(
            final Registration value,
            final JsonGenerator gen,
            final SerializerProvider serializers
        ) throws IOException {
            gen.writeStartObject(); // <1>
            this.delegate.serialize(value, gen, serializers);
            gen.writeEndObject();
        }

        @Override
        public JsonSerializer<Registration> unwrappingSerializer(// <2>
            final NameTransformer nameTransformer
        ) {
            return new UnwrappingRegistrationSerializer(nameTransformer);
        }
    }
    // end::delegating-serializer[]
    // tag::json-module-example[]
}
// end::json-module-example[]
