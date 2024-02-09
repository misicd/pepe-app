package org.dmship.model;

import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public final class PersonSpecificationBuilder {

    public static Specification<Person> createPersonSpecification(PersonSearchCriteria personSearchCriteria) {
        return firstNameEqualTo(personSearchCriteria.firstName())
                .and(lastNameEqualTo(personSearchCriteria.lastName()));
    }

    public static Specification<Person> firstNameEqualTo(Optional<String> firstName) {
        if (firstName == null) {
            return (root, query, builder) -> null;
        }

        return (root, query, builder) -> {
            return firstName
                    .map(firstNameVal -> builder.equal(root.get(Person_.firstName), firstNameVal))
                    .orElse(null);
        };
    }

    public static Specification<Person> lastNameEqualTo(Optional<String> lastName) {
        if (lastName == null) {
            return (root, query, builder) -> null;
        }

        return (root, query, builder) -> {
            return lastName
                    .map(lastNameVal -> builder.equal(root.get(Person_.lastName), lastNameVal))
                    .orElse(null);
        };
    }
}
