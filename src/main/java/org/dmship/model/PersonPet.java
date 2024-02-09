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

    @EmbeddedId
    private PersonPetId id;

    @ManyToOne
    @MapsId("personId")
    //@JoinColumn(name = "person_id", nullable = false, insertable = false, updatable = false)
    private Person person;

    @OneToOne
    @MapsId("petId")
    private Pet pet;

    public PersonPet(Person person, Pet pet) {
        this.person = person;
        this.pet = pet;
        this.id = new PersonPetId(person.getId(), pet.getId());
    }

    @Override
    public String toString() {
        return "PersonPet {" +
                "id=" + id +
                ", person='" + (person != null ? person.getFullName() : "null") +
                "', pet='" + (pet != null ? pet.getName() : "null") + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PersonPet that = (PersonPet) o;
        return Objects.equals(person, that.person) &&
                Objects.equals(pet, that.pet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, pet);
    }

    public void setPet(Pet pet) {
        if (this.pet != null) {
            this.pet.setPersonPet(null);
        }

        this.pet = pet;
    }
}
