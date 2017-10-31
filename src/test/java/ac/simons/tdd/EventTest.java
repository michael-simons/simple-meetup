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
package ac.simons.tdd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Michael J. Simons, 2017-10-31
 */
// tag::eventStructureTest[]
@TestInstance(Lifecycle.PER_CLASS) // <1>
class EventTest {
  @BeforeAll // <2>
  public void prepareEventClock() {
    Event.clock = Clock.fixed(Instant.parse("2018-01-01T08:00:00.00Z"), ZoneId.systemDefault());
  }

  @Nested // <3>
  class Preconditions {
    @TestFactory // <4>
    Stream<DynamicTest> constructor() {
      return Stream.of(LocalDate.of(2017, 10, 31), null) // <5>
          .map(date -> dynamicTest(
              "Constructor should not allow invalid events", () ->
                  assertEquals("Event requires a date in the future.",
                      assertThrows(
                          IllegalArgumentException.class,
                          () -> new Event(date, "test")
                      ).getMessage()))
          );
    }

    // end::eventStructureTest[]
    @TestFactory
    Stream<DynamicTest> setName() {
      final Event event = new Event(LocalDate.of(2018, 1, 2), "test", 20);
      return Stream.of(null, "", "\t", " ")
          .map(name -> dynamicTest(
              "setName should not accept invalid names", () ->
                  assertEquals("Event requires a non-empty name.",
                      assertThrows(
                          IllegalArgumentException.class,
                          () -> event.setName(name)
                      ).getMessage()))
          );
    }
    // tag::eventStructureTest[]
  }

  @Nested // <6>
  class Postconditions {
    @TestFactory
    Stream<DynamicTest> constructor() {
      return Stream.of(23, null)
          .map(numberOfSeats -> dynamicTest(
              "Constructor should create valid events", () -> {
                final Event event = new Event(LocalDate.of(2018, 1, 2), "test", numberOfSeats);
                assertAll( // <7>
                    () -> assertTrue(event.isOpen()),
                    () -> assertEquals(
                        Optional.ofNullable(numberOfSeats).orElse(20),
                        event.getNumberOfSeats()
                    )
                );
              })
          );
    }
  }
}
// end::eventStructureTest[]