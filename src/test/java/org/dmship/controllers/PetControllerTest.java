package org.dmship.controllers;

import org.dmship.PepeApplication;
import org.dmship.config.PepeApplicationFeatures;
import org.dmship.dto.PetDTO;
import org.dmship.exceptions.ResourceConflictException;
import org.dmship.services.PetService;
import org.dmship.util.DbResetService;
import org.dmship.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.togglz.junit5.AllEnabled;
import org.togglz.testing.TestFeatureManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PepeApplication.class)
@AutoConfigureMockMvc
class PetControllerTest {

    @Autowired
    DbResetService dbResetService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PetService petService;

    @BeforeEach
    void beforeEach() {
        dbResetService.resetDatabase();
    }

    @Test
    @DisplayName("S2: Store pet data")
    public void givenNewPet_whenPersistingPet_thenPetStored() throws Exception {
        //Given (preconditions)
        PetDTO petDTO = new PetDTO("Caesar", 3);

        //When (actions)
        ResultActions resultActions = mvc.perform(post("/pepe/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(petDTO)))
                .andExpect(status().isCreated());

        MvcResult result = resultActions.andReturn();
        String petIdAsString = result.getResponse().getContentAsString();
        Long petId = Long.parseLong(petIdAsString);

        PetDTO petDTOFound = petService.findById(petId).get();

        //Then (postconditions)
        assertEquals(petDTOFound.name(), petDTO.name());
        assertEquals(petDTOFound.age(), petDTO.age());
    }

    @Test
    @DisplayName("C2: Delete pet's data (feature DELETE_DOG enabled)")
    @AllEnabled(PepeApplicationFeatures.class)
    public void givenPet_whenDeletingPet_thenPetDeleted(TestFeatureManager featureManager) throws Exception {
        //Given (preconditions)
        featureManager.enable(PepeApplicationFeatures.DELETE_PET);

        PetDTO petDTO = new PetDTO("Caesar", 3);

        //When (actions)
        ResultActions resultActions = mvc.perform(post("/pepe/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(petDTO)))
                .andExpect(status().isCreated());

        MvcResult result = resultActions.andReturn();
        String petIdAsString = result.getResponse().getContentAsString();
        Long petId = Long.parseLong(petIdAsString);

        mvc.perform(delete("/pepe/v1/pets/{petId}", petId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> petService.deletePet(petId));

        //Then (postconditions)
        String errorMessage = "Pet with id '" + petId + "' not found";
        assertEquals(exception.getMessage(), errorMessage);
    }

    @Test
    @DisplayName("C2: Not allowed to delete pet's data (feature DELETE_DOG disabled)")
    @AllEnabled(PepeApplicationFeatures.class)
    public void givenPet_whenDeletingPet_thenErrorMethodNotAllowed(TestFeatureManager featureManager) throws Exception {
        //Given (preconditions)
        featureManager.disable(PepeApplicationFeatures.DELETE_PET);

        PetDTO petDTO = new PetDTO("Caesar", 3);

        //When (actions)
        ResultActions postResultActions = mvc.perform(post("/pepe/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(petDTO)))
                .andExpect(status().isCreated());

        MvcResult result = postResultActions.andReturn();
        String petIdAsString = result.getResponse().getContentAsString();
        Long petId = Long.parseLong(petIdAsString);

        ResultActions deleteResultActions = mvc.perform(delete("/pepe/v1/pets/{petId}", petId)
                .contentType(MediaType.APPLICATION_JSON));

        //Then (postconditions)
        deleteResultActions.andExpect(status().isMethodNotAllowed());
    }
}