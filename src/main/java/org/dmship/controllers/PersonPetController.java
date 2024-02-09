package org.dmship.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dmship.dto.PetsDTO;
import org.dmship.services.PersonPetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/pepe/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PersonPetController {

    private final PersonPetService personPetService;

    private static Logger logger = LoggerFactory.getLogger(PersonPetController.class);

    @Tag(name = "Add Existing Pet To The Person", description = "Person Pets")
    @Operation(description = "Add existing pet to the owner (person).")
    @PostMapping(value="/persons/{personId}/pets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPersonPet(@PathVariable("personId") Long personId,
                                          @Valid @RequestBody Long petId) {
        this.personPetService.addPersonPet(personId, petId);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }


    @Tag(name = "Remove Existing Pet From The Person", description = "Person Pets")
    @Operation(description = "Remove existing pet from the owner (person).")
    @DeleteMapping(value="/persons/{personId}/pets/{petId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removePersonPet(@PathVariable("personId") Long personId,
                                                 @PathVariable("personId") Long petId) {
        this.personPetService.removePersonPet(personId, petId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Tag(name = "Retrieve All Person's Pets", description = "Person Pets")
    @Operation(description = "Retrieve all person's pets")
    @GetMapping(value="/persons/{personId}/pets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PetsDTO> retrieveAllPersonPets(@PathVariable("personId") Long personId) {
        PetsDTO petsDTO = this.personPetService.retrieveAllPersonPets(personId);
        return new ResponseEntity<>(petsDTO, HttpStatus.OK);
    }
}
