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

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
@SuppressWarnings({"checkstyle:DesignForExtension"})
// tag::eventStructure[]
public class Event implements Serializable {

    public enum Status {

        open, closed
    }

    static final ThreadLocal<Clock> CLOCK =
        ThreadLocal.withInitial(() -> Clock.systemDefaultZone()); // <1>

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

    public Event(final LocalDate heldOn, final String name) {
        this(heldOn, name, 20);
    }

    // tag::eventStructure[]
    public Event(final LocalDate heldOn, final String name, final Integer numberOfSeats) {
        if (heldOn == null || heldOn.isBefore(LocalDate.now(CLOCK.get()))) {
            throw new IllegalArgumentException("Event requires a date in the future.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Event requires a non-empty name.");
        }

        this.heldOn = heldOn;
        this.name = name;

        this.setNumberOfSeats(numberOfSeats);

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

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(final Integer numberOfSeats) {
        if (numberOfSeats == null || numberOfSeats < 0) {
            throw new IllegalArgumentException("Event requires some seats.");
        }

        this.numberOfSeats = numberOfSeats;
    }

    public boolean isPastEvent() {
        return this.heldOn.isBefore(LocalDate.now(CLOCK.get()));
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
    public void registerFor(final Registration registration) {
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
        if (this.registrations.containsKey(registration.getEmail())) {
            throw new IllegalArgumentException("Already registered with email-addess" + registration.getEmail());
        }
        // tag::eventStructure[]
        this.registrations.put(registration.getEmail(), registration.getName());
    }
    // end::eventStructure[]

    Example<Event> asExample() {
        return Example.of(this, ExampleMatcher.matching()
              .withIgnoreNullValues()
              .withIgnorePaths("numberOfSeats", "status")
              .withMatcher("heldOn", match -> match.exact())
              .withMatcher("name", match -> match.exact())
        );
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.heldOn);
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
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
