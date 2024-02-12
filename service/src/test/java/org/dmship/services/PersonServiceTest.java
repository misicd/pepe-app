package org.dmship.services;

import org.dmship.dto.PersonUpdateDTO;
import org.dmship.model.Person;
import org.dmship.util.DbResetService;
import org.dmship.dto.PersonDTO;
import org.dmship.exceptions.ResourceConflictException;
import org.dmship.model.PersonSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonServiceTest {

    @Autowired
    DbResetService dbResetService;

    @Autowired
    PersonService personService;

    @BeforeEach
    void beforeEach() {
        dbResetService.resetDatabase();
    }

    @Test
    @DisplayName("M1, M3: Store person data. Retrieve single stored person by its Id")
    public void givenNewPerson_whenPersistingPerson_thenPersonStored() {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");

        //When (actions)
        Long personId1 = personService.createPerson(personDTO);
        PersonDTO personDTOFound = personService.findById(personId1).get();

        //Then (postconditions)
        assertEquals(personDTOFound.firstName(), personDTO.firstName());
        assertEquals(personDTOFound.lastName(), personDTO.lastName());
        assertEquals(personDTOFound.dateOfBirth(), personDTO.dateOfBirth());
        assertEquals(personDTOFound.address(), personDTO.address());
    }


    @Test
    @DisplayName("M1: The first name and last name of a person form a unique combination")
    public void givenTwoPersonsWithSameName_whenCreatePersons_thenSecondPersonCreationFails() {
        //Given (preconditions)

        String firstName = "Jan";
        String lastName = "Jansen";
        PersonDTO personDTO = new PersonDTO(firstName, lastName, LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");
        PersonDTO personDTO2 = new PersonDTO(firstName, lastName, LocalDate.of(1986, 3, 12),
                "Tweede Palensteinhof 35, 2804 GP Gouda");

        //When (actions)
        personService.createPerson(personDTO);

        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> personService.createPerson(personDTO2));

        //Then (postconditions)
        String errorMessage = "Could not create person, another person with the same name '"
                + Person.createFullName(firstName, lastName) + "' already exists";
        assertEquals(exception.getMessage(), errorMessage);
    }

    @Test
    @DisplayName("M2: Retrieve all stored persons")
    public void givenTwoPersons_whenCreatePersonsAndRetrievePersons_thenAllStoredPersonsRetrieved() {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");
        PersonDTO personDTO2 = new PersonDTO("Laura", "Ramos", LocalDate.of(1986, 3, 12),
                "Tweede Palensteinhof 35, 2804 GP Gouda");

        //When (actions)
        personService.createPerson(personDTO);
        personService.createPerson(personDTO2);

        // no filters
        PersonSearchCriteria personSearchCriteria = PersonSearchCriteria.builder().build();

        List<PersonDTO> personDTOs = personService.retrievePersons(personSearchCriteria);

        //Then (postconditions)
        assertEquals(personDTOs.size(), 2);
        assertTrue(personDTOs.contains(personDTO));
        assertTrue(personDTOs.contains(personDTO2));
    }

    @Test
    @DisplayName("M4: Update person's address")
    public void givenPersonWithUpdate_whenUpdatePerson_thenAllUpdatesSaved() {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");

        PersonUpdateDTO personUpdateDTO = new PersonUpdateDTO("Begijnekade 15, 3512 VV Utrecht");

        //When (actions)
        Long personId = personService.createPerson(personDTO);

        personService.updatePerson(personId, personUpdateDTO);
        PersonDTO personDTOUpdated = personService.findById(personId).get();

        //Then (postconditions)
        assertEquals(personDTOUpdated.firstName(), personDTO.firstName());
        assertEquals(personDTOUpdated.lastName(), personDTO.lastName());
        assertEquals(personDTOUpdated.dateOfBirth(), personDTO.dateOfBirth());
        assertNotEquals(personDTOUpdated.address(), personDTO.address());
        assertEquals(personDTOUpdated.address(), personUpdateDTO.address());
    }

    @Test
    @DisplayName("M4: Try to update person's address using nonexisting Id")
    public void givenPersonWithUpdate_whenUpdatePersonUsingNonexistingId_thenAllUpdatesSaved() {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");

        PersonUpdateDTO personUpdateDTO = new PersonUpdateDTO("Begijnekade 15, 3512 VV Utrecht");

        //When (actions)
        Long personId = personService.createPerson(personDTO);
        Long personIdNonexisting = personId + 1;

        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> personService.updatePerson(personIdNonexisting, personUpdateDTO));

        //Then (postconditions)
        String errorMessage = "Person with id '" + personIdNonexisting + "' not found";
        assertEquals(exception.getMessage(), errorMessage);
    }

    @Test
    @DisplayName("M5: Retrieve stored person by its first name")
    public void givenTwoPersonsDifferentFirstName_whenRetrievePersonByFirstName_thenOnlyFilteredPersonRetrieved() {
        //Given (preconditions)

        String firstNameUnique = "Jan";
        String lastNameSame = "Jansen";
        PersonDTO personDTO = new PersonDTO("Jan", lastNameSame, LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");
        PersonDTO personDTO2 = new PersonDTO("Fionna", lastNameSame, LocalDate.of(1986, 3, 12),
                "Tweede Palensteinhof 35, 2804 GP Gouda");

        //When (actions)
        personService.createPerson(personDTO);
        personService.createPerson(personDTO2);

        // no filters
        PersonSearchCriteria personSearchCriteria = PersonSearchCriteria.builder()
                .firstName(Optional.of(firstNameUnique)).build();

        List<PersonDTO> personDTOs = personService.retrievePersons(personSearchCriteria);

        //Then (postconditions)
        assertEquals(personDTOs.size(), 1);
        assertTrue(personDTOs.contains(personDTO));
    }

    @Test
    @DisplayName("M5: Retrieve stored person by its last name")
    public void givenTwoPersonsDifferentLastName_whenRetrievePersonByLastName_thenOnlyFilteredPersonRetrieved() {
        //Given (preconditions)

        String firstNameSame = "Jan";
        String lastNameUnique = "Dijstelbloem";
        PersonDTO personDTO = new PersonDTO(firstNameSame, "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");
        PersonDTO personDTO2 = new PersonDTO(firstNameSame, lastNameUnique, LocalDate.of(1986, 3, 12),
                "Tweede Palensteinhof 35, 2804 GP Gouda");

        //When (actions)
        personService.createPerson(personDTO);
        personService.createPerson(personDTO2);

        // no filters
        PersonSearchCriteria personSearchCriteria = PersonSearchCriteria.builder()
                .lastName(Optional.of(lastNameUnique)).build();

        List<PersonDTO> personDTOs = personService.retrievePersons(personSearchCriteria);

        //Then (postconditions)
        assertEquals(personDTOs.size(), 1);
        assertTrue(personDTOs.contains(personDTO2));
    }

    @Test
    @DisplayName("M5: Retrieve stored person by its first name and the last name")
    public void givenFewPersons_whenRetrievePersonByFirstNameAndLastName_thenOnlyFilteredPersonRetrieved() {
        //Given (preconditions)

        String firstNameFilter = "Laurens";
        String lastNameFilter = "Dijstelbloem";
        PersonDTO personDTO = new PersonDTO(firstNameFilter, "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");
        PersonDTO personDTO2 = new PersonDTO(firstNameFilter, lastNameFilter, LocalDate.of(1986, 3, 12),
                "Tweede Palensteinhof 35, 2804 GP Gouda");
        PersonDTO personDTO3 = new PersonDTO("Sem", lastNameFilter, LocalDate.of(1966, 5, 9),
                "Begijnekade 15, 3512 VV Utrecht");

        //When (actions)
        personService.createPerson(personDTO);
        personService.createPerson(personDTO2);

        // no filters
        PersonSearchCriteria personSearchCriteria = PersonSearchCriteria.builder()
                .firstName(Optional.of(firstNameFilter))
                .lastName(Optional.of(lastNameFilter))
                .build();

        List<PersonDTO> personDTOs = personService.retrievePersons(personSearchCriteria);

        //Then (postconditions)
        assertEquals(personDTOs.size(), 1);
        assertTrue(personDTOs.contains(personDTO2));
    }
}