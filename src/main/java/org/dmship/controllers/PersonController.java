package org.dmship.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dmship.dto.PersonUpdateDTO;
import org.dmship.model.PersonSearchCriteria;
import org.dmship.services.PersonService;
import org.dmship.dto.PersonDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/pepe/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    private static Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Tag(name = "Create Person", description = "Persons")
    @Operation(description = "Create new person. The combination of the first name and the last name must be unique. " +
            "If successful, returns person id.")
    @PostMapping(value="/persons", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        Long personId = this.personService.createPerson(personDTO);
        return new ResponseEntity<Long>(personId, HttpStatus.CREATED);
    }

    @Tag(name = "Update Person's Address", description = "Persons")
    @Operation(description = "Update the current living address of the existing person based on person id obtained when the person was created")
    @PatchMapping(value="/persons/{personId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>  updatePerson(@PathVariable("personId") Long personId,
                                              @Valid @RequestBody PersonUpdateDTO personUpdateDTO) {
        this.personService.updatePerson(personId, personUpdateDTO);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Tag(name = "Retrieve Person by Id", description = "Persons")
    @Operation(description = "Retrieve the person using person id obtained when the person was created")
    @PutMapping(value="/persons/{personId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTO>  retrievePerson(@PathVariable("personId") Long personId) {
        PersonDTO personDTO = this.personService.retrievePerson(personId);
        return new ResponseEntity<>(personDTO, HttpStatus.OK);
    }

    @Tag(name = "Retrieve Persons", description = "Persons")
    @Operation(description = "Retrieve person based on number of optional filter criteria supplied as request param(s):\n" +
            "1. first name - include only persons whose first name matches the specified first name\n" +
            "2. last name - include only persons whose last name matches the specified last name\n" +
            "\n" +
            "If no filter criteria is supplied, retrieve all persons.")
    @GetMapping(value="/persons", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonDTO>> retrievePersons(
            @RequestParam(required = false)
            Optional<String> firstName,
            @RequestParam(required = false)
            Optional<String> lastName) {
        PersonSearchCriteria personSearchCriteria = PersonSearchCriteria.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();

        List<PersonDTO> personDTOs = this.personService.retrievePersons(personSearchCriteria);
        return new ResponseEntity<>(personDTOs, HttpStatus.OK);
    }
}
