package org.dmship.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "person",
        uniqueConstraints = @UniqueConstraint(columnNames={"first_name", "last_name"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100)
    private String firstName;

    @Column(nullable = false)
    @Size(max = 100)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Size(max = 300)
    private String address;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private List<PersonPet> personPets = new ArrayList<>();

    public static String createFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public String getFullName() {
        return createFullName(firstName, lastName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Person person = (Person) o;
        return Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFullName());
    }

    public PersonPet addPet(Pet pet) {
        PersonPet personPet = new PersonPet(this, pet);
        personPets.add(personPet);
        return personPet;
    }

    public PersonPet removePet(Pet pet) {
        PersonPet personPet = null;

        for (Iterator<PersonPet> iterator = personPets.iterator(); iterator.hasNext(); ) {
            personPet = iterator.next();

            if (personPet.getPet() == pet) {
                personPet.setPet(null);

                iterator.remove();
                return personPet;
            }
        }

        return personPet;
    }

    public void update(PersonUpdate personUpdate) {
        setAddress(personUpdate.address());
    }
}
