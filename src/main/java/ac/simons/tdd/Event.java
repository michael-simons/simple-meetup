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

import javax.persistence.*;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Michael J. Simons, 2017-10-31
 */
@Entity
@Table(
    name = "events",
    uniqueConstraints = {
        @UniqueConstraint(name = "events_uk", columnNames = {"held_on", "name"})
    }
)
// tag::eventStructure[]
public class Event implements Serializable {

  public enum Status {

    open, closed
  }

  static Clock clock = Clock.systemDefaultZone(); // <1>

  // end::eventStructure[]
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "held_on", nullable = false)
  // tag::eventStructure[]
  private LocalDate heldOn;

  // end::eventStructure[]
  @Column(length = 512, nullable = false)
  // tag::eventStructure[]
  private String name;

  // end::eventStructure[]
  @Column(name = "number_of_seats", nullable = false)
  // tag::eventStructure[]
  private Integer numberOfSeats;

  // end::eventStructure[]
  @Enumerated(EnumType.STRING)
  // tag::eventStructure[]
  private Status status;

  // end::eventStructure[]
  @ElementCollection
  @CollectionTable(name = "registrations")
  @JoinColumn(name = "event_id")
  @MapKeyColumn(name = "email", length = 1024, nullable = false)
  @Column(name = "name", length = 512, nullable = false)
  // tag::eventStructure[]
  private Map<String, String> registrations = new HashMap<>();

  // end::eventStructure[]
  Event() {
  }

  public Event(LocalDate heldOn, String name) {
    this(heldOn, name, null);
  }

  // tag::eventStructure[]
  public Event(LocalDate heldOn, String name, Integer numberOfSeats) {
    if (heldOn == null || heldOn.isBefore(LocalDate.now(clock))) {
      throw new IllegalArgumentException("Event requires a date in the future.");
    }

    this.heldOn = heldOn;

    this.setName(name);
    this.setNumberOfSeats(Optional.ofNullable(numberOfSeats).orElse(20));

    this.status = Status.open;
  }

  // end::eventStructure[]

  public Integer getId() {
    return id;
  }

  public LocalDate getHeldOn() {
    return heldOn;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Event requires a non-empty name.");
    }
    this.name = name;
  }

  public Integer getNumberOfSeats() {
    return numberOfSeats;
  }

  public void setNumberOfSeats(Integer numberOfSeats) {
    if (numberOfSeats != null && numberOfSeats < 0) {
      throw new IllegalArgumentException("Event requires some seats.");
    }

    this.numberOfSeats = numberOfSeats;
  }

  public boolean isPastEvent() {
    return this.heldOn.isBefore(LocalDate.now(clock));
  }

  public boolean isOpen() {
    return this.status == Status.open;
  }

  public boolean isClosed() {
    return this.status == Status.closed;
  }

  public boolean isFull() {
    return this.registrations.size() == this.numberOfSeats;
  }

  public void close() {
    this.status = Status.closed;
  }

  // tag::eventStructure[]
  public void register(final String email, final String name) {
    // end::eventStructure[]
    if (isClosed()) {
      throw new IllegalStateException("Cannot register for a closed event.");
    }
    // tag::eventStructure[]
    if (isPastEvent()) {
      throw new IllegalStateException("Cannot register for a past event.");
    }
    if (isFull()) {
      throw new IllegalStateException("Cannot register for a full event.");
    }

    // Weitere Bedingungen ausgeblendet
    // end::eventStructure[]
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Registration requires a non-empty email-address.");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Registration requires a non-empty name.");
    }
    if (this.registrations.containsKey(email)) {
      throw new IllegalArgumentException("Already registered with email-addess" + email);
    }
    // tag::eventStructure[]

    this.registrations.put(email, name);
  }
  // end::eventStructure[]

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.heldOn);
    hash = 97 * hash + Objects.hashCode(this.name);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Event other = (Event) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    return Objects.equals(this.heldOn, other.heldOn);
  }

  // tag::eventStructure[]
}
// end::eventStructure[]
