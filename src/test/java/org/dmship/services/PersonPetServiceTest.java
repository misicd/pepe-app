package org.dmship.services;

import org.dmship.dto.PersonDTO;
import org.dmship.dto.PersonUpdateDTO;
import org.dmship.dto.PetDTO;
import org.dmship.dto.PetsDTO;
import org.dmship.exceptions.ResourceConflictException;
import org.dmship.model.Person;
import org.dmship.model.PersonSearchCriteria;
import org.dmship.util.DbResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonPetServiceTest {

    @Autowired
    DbResetService dbResetService;

    @Autowired
    PersonService personService;

    @Autowired
    PetService petService;

    @Autowired
    PersonPetService personPetService;

    @BeforeEach
    void beforeEach() {
        dbResetService.resetDatabase();
    }

    @Test
    @DisplayName("C1: Link multiple pets to the owner")
    public void givenNewPersonAndPets_whenPersistingPersonPets_thenPersonPetsStored() {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");
        PetDTO petDTO = new PetDTO("Caesar", 3);
        PetDTO petDTO2 = new PetDTO("Jackie", 5);

        //When (actions)
        Long personId1 = personService.createPerson(personDTO);
        Long petId1 = petService.createPet(petDTO);
        Long petId2 = petService.createPet(petDTO2);

        personPetService.addPersonPet(personId1, petId1);
        personPetService.addPersonPet(personId1, petId2);

        PetsDTO petsDTO = personPetService.retrieveAllPersonPets(personId1);

        //Then (postconditions)
        assertEquals(petsDTO.petIds().size(), 2);
        assertTrue(petsDTO.petIds().contains(petId1));
        assertTrue(petsDTO.petIds().contains(petId2));
    }

    @Test
    @DisplayName("C1: Unlink linked pets from the owner")
    public void givenNewPersonAndPets_whenRemovingPersonPets_thenPersonPetsRemoved() {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");
        PetDTO petDTO = new PetDTO("Caesar", 3);
        PetDTO petDTO2 = new PetDTO("Jackie", 5);

        //When (actions)
        Long personId1 = personService.createPerson(personDTO);
        Long petId1 = petService.createPet(petDTO);
        Long petId2 = petService.createPet(petDTO2);

        personPetService.addPersonPet(personId1, petId1);
        personPetService.addPersonPet(personId1, petId2);

        personPetService.removePersonPet(personId1, petId1);
        personPetService.removePersonPet(personId1, petId2);

        PetsDTO petsDTO = personPetService.retrieveAllPersonPets(personId1);

        //Then (postconditions)
        assertEquals(petsDTO.petIds().size(), 0);
    }

    @Test
    @DisplayName("C1: Can not link the same pet to two different owners")
    @Disabled("TODO: fix PersonPet/PersonPetId classes." +
            "person_pet is created with PK (person_id, pet_id) instead of specified PK (pet_id)")
    public void givenTwoPersonAndPet_whenAddingPetToTwoOwners_thenErrorNotAllowed() {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");
        PersonDTO personDTO2 = new PersonDTO("Laura", "Ramos", LocalDate.of(1986, 3, 12),
                "Tweede Palensteinhof 35, 2804 GP Gouda");
        PetDTO petDTO = new PetDTO("Caesar", 3);

        //When (actions)
        Long personId1 = personService.createPerson(personDTO);
        Long personId2 = personService.createPerson(personDTO2);
        Long petId1 = petService.createPet(petDTO);

        personPetService.addPersonPet(personId1, petId1);

        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> personPetService.addPersonPet(personId2, petId1));

        PetsDTO petsDTO = personPetService.retrieveAllPersonPets(personId1);
        PetsDTO petsDTO2 = personPetService.retrieveAllPersonPets(personId2);

        //Then (postconditions)
        String errorMessage = "Could not add pet with Id " + petId1 +
                " to the person with Id " + personId2 + ", db error";
        assertEquals(exception.getMessage(), errorMessage);

        assertEquals(petsDTO.petIds().size(), 1);
        assertTrue(petsDTO.petIds().contains(petId1));
        assertEquals(petsDTO2.petIds().size(), 0);
    }
}