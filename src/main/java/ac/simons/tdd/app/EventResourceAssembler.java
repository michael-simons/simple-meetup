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
package ac.simons.tdd.app;

import ac.simons.tdd.domain.Event;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Michael J. Simons, 2017-11-03
 */
@Component
final class EventResourceAssembler extends ResourceAssemblerSupport<Event, EventResource> {
    EventResourceAssembler() {
        super(EventsApi.class, EventResource.class);
    }

    @Override
    public EventResource toResource(final Event entity) {
        final EventResource resource = new EventResource(entity);
        final ControllerLinkBuilder linkBuilder =
            linkTo(methodOn(EventsApi.class).event(entity.getHeldOn(), entity.getName()));
        resource.add(linkBuilder.withRel("self"));
        resource.add(linkBuilder.withRel("event"));
        return resource;
    }
}
