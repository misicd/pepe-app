package org.dmship.model;

import jakarta.validation.constraints.Size;

public record PersonUpdate (

    @Size(max = 300)
    String address) {}
