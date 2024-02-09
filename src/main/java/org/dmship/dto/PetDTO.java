package org.dmship.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

public record PetDTO(

    @NotBlank(message = "cannot be empty.")
    @Size(max = 100, message = "cannot contain more than 100 characters.")
    String name,

    @NotNull(message = "cannot be empty.")
    @DecimalMin(value = "1", message = "pet can be at minimum 1 year old. If pet has less then 1 year, round up to 1.")
    @DecimalMax(value = "199", message = "pet can be at maximum 199 years old.")
    Integer age) {

    @Builder(toBuilder = true)
    public PetDTO {}
}
