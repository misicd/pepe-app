package org.dmship.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "person_pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonPet {

    @Id
    @Column(name = "pet_id")
    private Long petId;

    @Column(name = "person_id")
    private Long personId;

    @OneToOne
    @MapsId
    private Pet pet;

    public PersonPet(Person person, Pet pet) {
        this.petId = pet.getId();
        this.personId = person.getId();
        this.pet = pet;
    }

    @Override
    public String toString() {
        return "PersonPet {" +
                "petId=" + petId + ", personId=" + personId +
                "', pet='" + (pet != null ? pet.getName() : "null") +
                "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PersonPet that = (PersonPet) o;
        return Objects.equals(petId, that.petId) &&
                Objects.equals(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(petId, personId);
    }
}
