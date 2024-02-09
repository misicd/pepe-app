package org.dmship.services;

import org.dmship.dto.PetDTO;
import org.dmship.exceptions.ResourceConflictException;
import org.dmship.util.DbResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PetServiceTest {

    @Autowired
    DbResetService dbResetService;

    @Autowired
    PetService petService;

    @BeforeEach
    void beforeEach() {
        dbResetService.resetDatabase();
    }

    @Test
    @DisplayName("S2: Store pet data. Retrieve single stored pet by its Id")
    public void givenNewPet_whenPersistingPet_thenPetStored() {
        //Given (preconditions)
        PetDTO petDTO = new PetDTO("Caesar", 3);

        //When (actions)
        Long petId1 = petService.createPet(petDTO);
        PetDTO petDTOFound = petService.findById(petId1).get();

        //Then (postconditions)
        assertEquals(petDTOFound.name(), petDTO.name());
        assertEquals(petDTOFound.age(), petDTO.age());
    }


    @Test
    @DisplayName("S2: Store data for multiple pets with the same name. Pet name does not have to be unique.")
    public void givenTwoPetsWithSameName_whenCreatePets_thenBothPetsCreated() {
        //Given (preconditions)
        String petName = "Caesar";
        PetDTO petDTO = new PetDTO(petName, 3);
        PetDTO petDTO2 = new PetDTO(petName, 2);

        //When (actions)
        Long petId1 = petService.createPet(petDTO);
        Long petId2 = petService.createPet(petDTO2);

        PetDTO petDTOFound = petService.findById(petId1).get();
        PetDTO petDTOFound2 = petService.findById(petId2).get();

        //Then (postconditions)
        assertEquals(petDTOFound.name(), petName);
        assertEquals(petDTOFound.age(), petDTO.age());
        assertEquals(petDTOFound2.name(), petName);
        assertEquals(petDTOFound2.age(), petDTO2.age());
    }

    @Test
    @DisplayName("S3: Retrieve all stored pets")
    public void givenTwoPets_whenCreatePetsAndRetrievePets_thenAllStoredPetsRetrieved() {
        //Given (preconditions)
        String petName = "Caesar";
        PetDTO petDTO = new PetDTO(petName, 3);
        PetDTO petDTO2 = new PetDTO(petName, 2);

        //When (actions)
        petService.createPet(petDTO);
        petService.createPet(petDTO2);

        List<PetDTO> petDTOs = petService.retrieveAllPets();

        //Then (postconditions)
        assertEquals(petDTOs.size(), 2);
        assertTrue(petDTOs.contains(petDTO));
        assertTrue(petDTOs.contains(petDTO2));
    }

    @Test
    @DisplayName("S4: Update pet's data")
    public void givenPetWithUpdate_whenUpdatePet_thenAllUpdatesSaved() {
        //Given (preconditions)
        PetDTO petDTO = new PetDTO("Caesar", 3);
        PetDTO petUpdateDTO = new PetDTO("Caesar Sr", 4);

        //When (actions)
        Long petId = petService.createPet(petDTO);

        petService.updatePet(petId, petUpdateDTO);
        PetDTO petDTOUpdated = petService.findById(petId).get();

        //Then (postconditions)
        assertEquals(petDTOUpdated.name(), petUpdateDTO.name());
        assertEquals(petDTOUpdated.age(), petUpdateDTO.age());
    }


    @Test
    @DisplayName("S4: Try to update pet's data using nonexisting Id")
    public void givenPetWithUpdate_whenUpdatePetUsingNonexistingId_thenAllUpdatesSaved() {
        //Given (preconditions)
        PetDTO petDTO = new PetDTO("Caesar", 3);
        PetDTO petUpdateDTO = new PetDTO("Caesar Sr", 4);

        //When (actions)
        Long petId = petService.createPet(petDTO);
        Long petIdNonexisting = petId + 1;

        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> petService.updatePet(petIdNonexisting, petUpdateDTO));

        //Then (postconditions)
        String errorMessage = "Pet with id '" + petIdNonexisting + "' not found";
        assertEquals(exception.getMessage(), errorMessage);
    }

    @Test
    @DisplayName("C1: Delete pet's data")
    public void givenPet_whenDeletePet_thenPetDeleted() {
        //Given (preconditions)
        PetDTO petDTO = new PetDTO("Caesar", 3);

        //When (actions)
        Long petId = petService.createPet(petDTO);

        PetDTO petDTOFoundBeforeDelete = petService.findById(petId).get();

        petService.deletePet(petId);

        Optional<PetDTO> petDTONotFoundAfterDelete = petService.findById(petId);

        //Then (postconditions)
        assertEquals(petDTOFoundBeforeDelete.name(), petDTO.name());
        assertEquals(petDTOFoundBeforeDelete.age(), petDTO.age());
        assertTrue(petDTONotFoundAfterDelete.isEmpty());
    }
}