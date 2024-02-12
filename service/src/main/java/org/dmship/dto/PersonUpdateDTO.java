package org.dmship.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/** Currently we allow only updating of the person's address
 *
 * @param address
 */
public record PersonUpdateDTO(

        @NotBlank(message = "cannot be empty.")
        @Size(max = 300, message = "address cannot contain more than 300 characters.")
        String address) {

    @Builder(toBuilder = true)
    public PersonUpdateDTO {}
}

