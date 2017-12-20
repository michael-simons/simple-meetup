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
package ac.simons.simplemeetup.domain;

/**
 * Represents a person who wants to registrate for an event.
 *
 * @author Michael J. Simons, 2017-11-05
 */
public final class Person {
    private final String email;

    private final String name;

    public Person(final String email, final String name) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Person requires a non-empty email-address.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Person requires a non-empty name.");
        }

        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
