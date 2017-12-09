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
import ac.simons.simplemeetup.domain.EventService;
import ac.simons.simplemeetup.domain.NoSuchEventException;
import ac.simons.simplemeetup.domain.Person;
import ac.simons.simplemeetup.domain.Registration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.afford;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Michael J. Simons, 2017-10-31
 */
// tag::domain-usage-single-event[]
@RestController // <1>
@RequestMapping("/api/events") // <2>
// end::domain-usage-single-event[]
@SuppressWarnings({"checkstyle:DesignForExtension"})
// tag::domain-usage-single-event[]
public class EventsApi {

    private final EventService eventService;

    // end::domain-usage-single-event[]

    private final ResourceAssemblerSupport<Event, EventResource> eventResourceAssembler;

    public EventsApi(final EventService eventService) {
        this.eventService = eventService;
        this.eventResourceAssembler = EventResource.assembler();
    }

    @GetMapping
    public Resources<EventResource> events() {
        final List<EventResource> eventResources = eventResourceAssembler.toResources(this.eventService.getOpenEvents());
        return new Resources<>(eventResources, linkTo(methodOn(this.getClass()).events()).withSelfRel());
    }

    @PostMapping
    public HttpEntity<EventResource> createNewEvent(@RequestBody final Event newEvent) {
        final EventResource eventResource = this.eventResourceAssembler
            .toResource(this.eventService.createNewEvent(newEvent));
        return ResponseEntity.created(
            URI.create(eventResource.getId().map(Link::getHref).orElseThrow(InvalidResourceException::new))).body(eventResource);
    }

    // tag::domain-usage-single-event[]
    @GetMapping("/{heldOn}/{name}") // <3>
    public EventResource event(
        @PathVariable @DateTimeFormat(iso = ISO.DATE) // <4>
        final LocalDate heldOn,
        @PathVariable final String name
    ) {
        return this.eventService
            .getEvent(heldOn, name)
            .map(eventResourceAssembler::toResource)
            .orElseThrow(NoSuchEventException::new);
    }
    // end::domain-usage-single-event[]

    @GetMapping("/{heldOn}/{name}/registrations")
    public Resources<Registration> registrations(
        @PathVariable @DateTimeFormat(iso = ISO.DATE) final LocalDate heldOn,
        @PathVariable final String name
    ) {
        final Event event = this.eventService
            .getEvent(heldOn, name)
            .orElseThrow(NoSuchEventException::new);
        return new Resources<>(
            event.getRegistrations(),
            linkTo(methodOn(this.getClass()).registrations(event.getHeldOn(), event.getName()))
                .withSelfRel()
                .andAffordance(afford(methodOn(EventsApi.class).registerFor(event.getHeldOn(), event.getName(), null)))
        );
    }

    @PostMapping("/{heldOn}/{name}/registrations")
    public HttpEntity<Registration> registerFor(
        @PathVariable @DateTimeFormat(iso = ISO.DATE) final LocalDate heldOn,
        @PathVariable final String name,
        @RequestBody final Person person
    ) {
        return new ResponseEntity<>(
            this.eventService.registerFor(new Event(heldOn, name), person),
            HttpStatus.CREATED
        );
    }
    // tag::domain-usage-single-event[]
}
// end::domain-usage-single-event[]
