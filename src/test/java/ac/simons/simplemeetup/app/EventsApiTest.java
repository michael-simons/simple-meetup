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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Michael J. Simons, 2017-11-02
 */
// tag::domain-usage-single-event-test[]
@RunWith(SpringRunner.class) // <1>
@WebMvcTest(controllers = EventsApi.class) // <2>
@AutoConfigureRestDocs // <3>
public class EventsApiTest {

    @MockBean // <4>
    private EventService eventService;

    // end::domain-usage-single-event-test[]
    @Autowired
    private MockMvc mockMvc;

    private final LinksSnippet selfLink = links(linkWithRel("self").ignored().optional());
    // tag::domain-usage-single-event-test[]
    @Before
    public void initializeMocks() {
        final Event event1 = new Event(LocalDate.now(), "Event-1");
        // end::domain-usage-single-event-test[]
        when(eventService.getOpenEvents())
            .thenReturn(Arrays.asList(event1, new Event(LocalDate.now().plusDays(1), "Event-2")));
        // tag::domain-usage-single-event-test[]
        when(eventService.getEvent(event1.getHeldOn(), event1.getName()))
            .thenReturn(Optional.of(event1));
    }

    // end::domain-usage-single-event-test[]
    @Test
    public void eventsShouldWork() throws Exception {
        this.mockMvc
            .perform(get("/api/events").accept(HAL_JSON))
            .andExpect(status().isOk())
            .andDo(document("get-events",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                selfLink,
                responseFields(
                    subsectionWithPath("_embedded.events").description("An array of Event resources"),
                    subsectionWithPath("_links").description("Links to other resources")
                )));

    }

    // tag::domain-usage-single-event-test[]
    @Test
    public void eventShouldWork() throws Exception {
        this.mockMvc
            .perform(
                get("/api/events/{heldOn}/{name}",
                    LocalDate.now(), "Event-1"
                ).accept(HAL_JSON)) // <5>
            .andExpect(status().isOk()) // <6>
            .andDo(document("get-event", // <7>
                // end::domain-usage-single-event-test[]
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                selfLink.and(
                    linkWithRel("registrations").description("Registrations for this event.")),
                // tag::domain-usage-single-event-test[]
                responseFields(
                    fieldWithPath("heldOn").description("The date of this event."),
                    fieldWithPath("name").description("The name of this event."),
                    fieldWithPath("numberOfFreeSeats")
                        .description("Number of free seats left."),
                    subsectionWithPath("_links")
                        .description("Links to other resources")
                )));
    }
}
// end::domain-usage-single-event-test[]
