package org.dmship.model;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.LocalDate;

@StaticMetamodel(Person.class)
public class Person_ {

    public static volatile SingularAttribute<Person, Long> id;
    public static volatile SingularAttribute<Person, String> firstName;
    public static volatile SingularAttribute<Person, String> lastName;
    public static volatile SingularAttribute<Person, LocalDate> dateOfBirth;
    public static volatile SingularAttribute<Person, String> address;
    public static volatile ListAttribute<Person, PersonPet> personPets;
}
