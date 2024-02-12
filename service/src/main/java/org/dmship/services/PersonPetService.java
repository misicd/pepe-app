package org.dmship.services;

import lombok.RequiredArgsConstructor;
import org.dmship.dto.PetsDTO;
import org.dmship.exceptions.ResourceConflictException;
import org.dmship.exceptions.ResourceInternalException;
import org.dmship.model.Person;
import org.dmship.model.PersonPet;
import org.dmship.model.Pet;
import org.dmship.repository.PersonPetRepository;
import org.dmship.repository.PersonRepository;
import org.dmship.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonPetService {

    private static final Logger logger = LoggerFactory.getLogger(PersonPetService.class);
    private final PersonRepository personRepository;
    private final PetRepository petRepository;
    private final PersonPetRepository personPetRepository;

    @Transactional
    public void addPersonPet(Long personId, Long petId) {
        logger.debug("try add PersonPet: personId {}, petId {}", personId, petId);

        Optional<PersonPet> personPetFound = personPetRepository.findByPersonIdAndPetId(personId, petId);

        if (personPetFound.isPresent()) {
            String errorMessage = "Person with id '" + personId + "' already has Pet with id '" + petId + "'";
            logger.error("error {}, PersonPet:\n{}", errorMessage, personPetFound.get());

            throw new ResourceConflictException(errorMessage);
        }

        Optional<Person> person = personRepository.findById(personId);

        if (person.isEmpty()) {
            String errorMessage = "Person with id '" + personId + "' does not exist";
            logger.error("error {}", errorMessage);

            throw new ResourceConflictException(errorMessage);
        }

        Optional<Pet> pet = petRepository.findById(petId);

        if (pet.isEmpty()) {
            String errorMessage = "Pet with id '" + petId + "' does not exist";
            logger.error("error {}", errorMessage);

            throw new ResourceConflictException(errorMessage);
        }

        PersonPet personPet = person.get().addPet(pet.get());

        try {
            logger.info("save Person {} with linked pet {}", person.get(), pet.get());

            personRepository.saveAndFlush(person.get());
        } catch(org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("""
                    DataIntegrityViolationException exception when trying to create personPet:
                    {}
                    exception:{}""", personPet, e.getMessage());

            // TODO: implement analysis into the exact cause of DataIntegrityViolationException exception
            String errorMessage = "Could not add pet with Id " + petId +
                    " to the person with Id " + personId + ", db error";
            throw new ResourceConflictException(errorMessage);
        } catch(Exception e) {
            logger.error("""
                    General exception when trying to create personPet:
                    {}
                    exception:{}""", personPet, e.getMessage());

            String errorMessage = "Could not add pet with Id " + petId +
                    " to the person with Id " + personId + ", unexpected error";
            throw new ResourceInternalException(errorMessage);
        }
    }

    @Transactional
    public void removePersonPet(Long personId, Long petId) {
        Optional<PersonPet> personPetFound = personPetRepository.findByPersonIdAndPetId(personId, petId);

        if (personPetFound.isEmpty()) {
            String errorMessage = "Person with id '" + personId + "' does not have Pet with id '" + petId + "'";
            throw new ResourceConflictException(errorMessage);
        }

        Optional<Person> person = personRepository.findById(personId);

        if (person.isEmpty()) {
            String errorMessage = "Person with id '" + personId + "' does not exist";
            logger.error("error {}", errorMessage);

            throw new ResourceConflictException(errorMessage);
        }

        Optional<Pet> pet = petRepository.findById(petId);

        if (pet.isEmpty()) {
            String errorMessage = "Pet with id '" + petId + "' does not exist";
            logger.error("error {}", errorMessage);

            throw new ResourceConflictException(errorMessage);
        }

        PersonPet personPet = person.get().removePet(pet.get());

        try {
            logger.info("save Person {} with unlinked pet {}", person.get(), pet.get());

            personRepository.saveAndFlush(person.get());
        } catch(org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("""
                    DataIntegrityViolationException exception when trying to delete personPet:
                    {}
                    exception:{}""", personPet, e.getMessage());

            // TODO: implement analysis into the exact cause of DataIntegrityViolationException exception
            String errorMessage = "Could not delete personPet {}" + personPet + ", db error";
            throw new ResourceConflictException(errorMessage);
        } catch(Exception e) {
            logger.error("""
                    General exception when trying to delete personPet:
                    {}
                    exception:{}""", personPet, e.getMessage());

            String errorMessage = "Could not delete personPet '"
                    + personPet + "', unexpected error";
            throw new ResourceInternalException(errorMessage);
        }
    }


    @Transactional
    public PetsDTO retrieveAllPersonPets(Long personId) {
        PetsDTO petsDTO;

        try {
            List<PersonPet> personPets = personPetRepository.findByPersonId(personId);

            new ArrayList<>();
            List<Long> petIDs = personPets.stream()
                    .map(personPet -> personPet.getPetId())
                    .collect(Collectors.toList());

            logger.debug("retrieveAllPersonPets for personId {} - found following petIds:\n{}", personId, petIDs);
            petsDTO = new PetsDTO(petIDs);
        } catch(Exception e) {
            logger.error("General exception when trying to retrieve all pets for personId '{}'" +
                    "\nexception:{}", personId, e.getMessage());

            String errorMessage = "Could not retrieve pets, unexpected error";
            throw new ResourceInternalException(errorMessage);
        }

        return petsDTO;
    }
}
