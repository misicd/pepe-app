package org.dmship.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

public record PetsDTO(

    @Valid
    @NotEmpty(message = "list cannot be empty.")
    List<Long> petIds) {

    @Builder(toBuilder = true)
    public PetsDTO {}
}
