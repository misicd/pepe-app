package org.dmship.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dmship.services.PetService;
import org.dmship.dto.PetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/pepe/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    private static Logger logger = LoggerFactory.getLogger(PetController.class);

    @Tag(name = "Create Pet", description = "Pets")
    @Operation(description = "Create a new pet. If successful, returns pet id.")
    @PostMapping(value="/pets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createPet(@Valid @RequestBody PetDTO petDTO) {
        Long petId = this.petService.createPet(petDTO);
        return new ResponseEntity<Long>(petId, HttpStatus.CREATED);
    }

    @Tag(name = "Update Pet", description = "Pets")
    @Operation(description = "Update the pet data based on pet id obtained when the pet was created")
    @PutMapping(value="/pets/{petId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>  updatePet(@PathVariable("petId") Long petId,
                                              @Valid @RequestBody PetDTO petDTO) {
        this.petService.updatePet(petId, petDTO);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Tag(name = "Retrieve Pet by Id", description = "Pets")
    @Operation(description = "Retrieve the pet data using pet id obtained when the pet was created")
    @GetMapping(value="/pets/{petId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PetDTO>  retrievePet(@PathVariable("petId") Long petId) {
        PetDTO petDTO = this.petService.retrievePet(petId);
        return new ResponseEntity<>(petDTO, HttpStatus.OK);
    }

    @Tag(name = "Retrieve All Pets", description = "Pets")
    @Operation(description = "Retrieve all pets")
    @GetMapping(value="/pets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PetDTO>> retrievePets() {
        List<PetDTO> petDTOs = this.petService.retrieveAllPets();
        return new ResponseEntity<>(petDTOs, HttpStatus.OK);
    }
}
