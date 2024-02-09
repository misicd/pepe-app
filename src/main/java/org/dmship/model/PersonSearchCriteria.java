package org.dmship.model;

import lombok.Builder;

import java.util.Optional;

public record PersonSearchCriteria(

    /**
     * If defined, results should include only persons whose first name matches the specified 'firstName' value.
     * If null or optional is empty, ignore this filter criteria.
     */
    Optional<String> firstName,

    /**
     * If defined, results should include only persons whose last name matches the specified 'lastName' value.
     * If null or optional is empty, ignore this filter criteria.
     */
    Optional<String> lastName) {

    @Builder(toBuilder = true)
    public PersonSearchCriteria {}
}
