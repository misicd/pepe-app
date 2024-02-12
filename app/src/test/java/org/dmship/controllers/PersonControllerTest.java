package org.dmship.controllers;

import org.dmship.PepeApplication;
import org.dmship.dto.PersonDTO;
import org.dmship.dto.PersonUpdateDTO;
import org.dmship.services.PersonService;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PepeApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    DbResetService dbResetService;

    @Autowired
    private PersonService personService;

    @BeforeEach
    void beforeEach() {
        dbResetService.resetDatabase();
    }

    @Test
    @DisplayName("M1, M3: Store person data. Retrieve single stored person by its Id")
    public void givenNewPerson_whenPersistingPerson_thenPersonStored() throws Exception {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");

        //When (actions)
        ResultActions resultActions = mvc.perform(post("/pepe/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(personDTO)))
                .andExpect(status().isCreated());

            MvcResult result = resultActions.andReturn();
        String personIdAsString = result.getResponse().getContentAsString();
        Long personId = Long.parseLong(personIdAsString);

        PersonDTO personDTOFound = personService.findById(personId).get();

        //Then (postconditions)
        assertEquals(personDTOFound.firstName(), personDTO.firstName());
        assertEquals(personDTOFound.lastName(), personDTO.lastName());
        assertEquals(personDTOFound.dateOfBirth(), personDTO.dateOfBirth());
        assertEquals(personDTOFound.address(), personDTO.address());
    }

    @Test
    @DisplayName("M2: Retrieve all stored persons")
    public void givenTwoPersons_whenCreatePersonsAndRetrievePersons_thenAllStoredPersonsRetrieved()  throws Exception {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");
        PersonDTO personDTO2 = new PersonDTO("Laura", "Ramos", LocalDate.of(1986, 3, 12),
                "Tweede Palensteinhof 35, 2804 GP Gouda");

        //When (actions)
        mvc.perform(post("/pepe/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(personDTO)))
                .andExpect(status().isCreated());

        mvc.perform(post("/pepe/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(personDTO2)))
                .andExpect(status().isCreated());

        //Then (postconditions)
        // retrieve persons without any filters
        mvc.perform(get("/pepe/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[" +
                        "{\"firstName\":\"Jan\"," +
                        "\"lastName\":\"Jansen\"," +
                        "\"dateOfBirth\":[1980,6,18]," +
                        "\"address\":\"Kalverhoeve 41, 3992 NX Houten\"}," +
                        "{\"firstName\":\"Laura\"," +
                        "\"lastName\":\"Ramos\"," +
                        "\"dateOfBirth\":[1986,3,12]," +
                        "\"address\":\"Tweede Palensteinhof 35, 2804 GP Gouda\"}]"))
                .andExpect(jsonPath("$[?(@.firstName == \"Jan\")].lastName").value("Jansen"))
                .andExpect(jsonPath("$[?(@.firstName == \"Laura\")].lastName").value("Ramos"));
    }

    @Test
    @DisplayName("S1: Update person address forbidden for regular (non-admin) user")
    @WithUserDetails
    public void givenPerson_whenUpdatePersonAsUser_thenAccessForbidden() throws Exception {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");

        PersonUpdateDTO personUpdateDTO = new PersonUpdateDTO("Begijnekade 15, 3512 VV Utrecht");

        //When (actions)
        ResultActions resultActions = mvc.perform(post("/pepe/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(personDTO)))
                .andExpect(status().isCreated());

        MvcResult result = resultActions.andReturn();
        String personIdAsString = result.getResponse().getContentAsString();
        Long personId = Long.parseLong(personIdAsString);

        ResultActions resultActionsPatch = mvc.perform(patch("/pepe/v1/persons/{personId}", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(personUpdateDTO)));

        //Then (postconditions)
        resultActionsPatch.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("S1: Update person address allowed for admins")
    @WithUserDetails(value = "admin")
    public void givenPerson_whenUpdatePersonAsAdmin_thenPersonUpdated() throws Exception {
        //Given (preconditions)
        PersonDTO personDTO = new PersonDTO("Jan", "Jansen", LocalDate.of(1980, 6, 18),
                "Kalverhoeve 41, 3992 NX Houten");

        PersonUpdateDTO personUpdateDTO = new PersonUpdateDTO("Begijnekade 15, 3512 VV Utrecht");

        //When (actions)
        ResultActions resultActions = mvc.perform(post("/pepe/v1/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(personDTO)))
                .andExpect(status().isCreated());

        MvcResult result = resultActions.andReturn();
        String personIdAsString = result.getResponse().getContentAsString();
        Long personId = Long.parseLong(personIdAsString);

        ResultActions resultActionsPatch = mvc.perform(patch("/pepe/v1/persons/{personId}", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(personUpdateDTO)));

        //Then (postconditions)
        resultActionsPatch.andExpect(status().isOk());
    }
}