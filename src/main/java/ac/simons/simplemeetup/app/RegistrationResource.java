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
import ac.simons.simplemeetup.domain.Registration;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

/**
 * @author Michael J. Simons, 2017-12-20
 */
@Relation(value = "registration", collectionRelation = "registrations")
public class RegistrationResource extends ResourceSupport {

    static ResourceAssemblerSupport<Registration, RegistrationResource> assembler(final Event event) {
        return new ResourceAssemblerSupport<Registration, RegistrationResource>(EventsApi.class, RegistrationResource.class) {
            @Override
            public RegistrationResource toResource(final Registration entity) {
                // TODO That would be the point to add unregister links etc.
                return new RegistrationResource(entity);
            }
        };
    }

    @JsonUnwrapped
    private final Registration registration;

    RegistrationResource(final Registration registration) {
        this.registration = registration;
    }
}
