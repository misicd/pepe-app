package org.dmship.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

public record PersonDTO (

        @NotBlank(message = "cannot be empty.")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "cannot be empty.")
        @Size(max = 100)
        String lastName,

        // TODO: implement date validation (valid format, date in the past)
        @NotNull(message = "cannot be empty.")
        @JsonSerialize(using = LocalDateSerializer.class)
        LocalDate dateOfBirth,

        @NotBlank(message = "cannot be empty.")
        @Size(max = 300, message = "address cannot contain more than 300 characters.")
        String address) {

    @Builder(toBuilder = true)
    public PersonDTO {}
}

