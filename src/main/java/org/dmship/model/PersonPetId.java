package org.dmship.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonPetId implements Serializable {

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "pet_id")
    private Long petId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PersonPetId that = (PersonPetId) o;
        return Objects.equals(personId, that.personId) &&
                Objects.equals(petId, that.petId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personId, petId);
    }
}
