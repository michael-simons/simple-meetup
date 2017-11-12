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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Locale;

/**
 * Represents a registration value.
 *
 * @author Michael J. Simons, 2017-10-31
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(of = {"email", "name"})
public final class Registration {
    @Column(name = "email", length = 1024, nullable = false)
    private String email;

    @Column(name = "name", length = 512, nullable = false)
    private String name;

    public Registration(final Person person) {
        this.email = person.getEmail().toLowerCase(Locale.ENGLISH);
        this.name = person.getName();
    }
}
