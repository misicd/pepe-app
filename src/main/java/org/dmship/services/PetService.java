package org.dmship.services;

import lombok.RequiredArgsConstructor;
import org.dmship.dto.PetDTO;
import org.dmship.exceptions.ResourceConflictException;
import org.dmship.exceptions.ResourceInternalException;
import org.dmship.mapping.PetMapper;
import org.dmship.model.Pet;
import org.dmship.repository.PersonRepository;
import org.dmship.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);
    private final PetMapper petMapper;
    private final PetRepository petRepository;
    private final PersonRepository personRepository;

    public Optional<PetDTO> findById(Long petId) {
        Optional<Pet> pet = petRepository.findById(petId);

        if (pet.isEmpty()) {
            return Optional.empty();
        }

        PetDTO petDTO = petMapper.toDTO(pet.get());
        return Optional.of(petDTO);
    }

    @Transactional
    public Long createPet(PetDTO petDTO) {
        Pet pet = petMapper.toEntity(petDTO);

        try {
            logger.debug("create pet before save:\n{}", pet);
            pet = petRepository.saveAndFlush(pet);
            logger.debug("created pet:\n{}", pet);
        } catch(org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolationException exception when trying to create pet:\n{}" +
                    "\nexception:{}", pet, e.getMessage());

            // TODO: implement analysis into the exact cause of DataIntegrityViolationException exception
            String errorMessage = "Could not create pet, another pet with the same name '"
                    + pet.getName() + "' already exists";
            throw new ResourceConflictException(errorMessage);
        } catch(Exception e) {
            logger.error("General exception when trying to create pet:\n{}" +
                    "\nexception:{}", pet, e.getMessage());

            String errorMessage = "Could not create pet with the name '"
                    + pet.getName() + "', unexpected error";
            throw new ResourceInternalException(errorMessage);
        }

        return pet.getId();
    }

    @Transactional
    public void updatePet(Long petId, PetDTO petDTO) {
        Optional<Pet> petFound = petRepository.findById(petId);

        if (petFound.isEmpty()) {
            String errorMessage = "Pet with id '" + petId + "' not found";
            throw new ResourceConflictException(errorMessage);
        }

        Pet pet = petFound.get();

        try {
            Pet petUpdate = petMapper.toEntity(petDTO);
            pet.update(petUpdate);

            logger.debug("update pet before save:\n{}", pet);
            Pet petUpdated = petRepository.saveAndFlush(pet);
            logger.debug("update pet after save:\n{}", petUpdated);
        } catch(org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolationException exception when trying to update pet:\n{}" +
                    "\nexception:{}", pet, e.getMessage());

            // TODO: implement analysis into the exact cause of DataIntegrityViolationException exception
            String errorMessage = "Could not update pet, another pet with the name '"
                    + pet.getName() + "' equal to updated name already exists";
            throw new ResourceConflictException(errorMessage);
        } catch(Exception e) {
            logger.error("General exception when trying to update pet with id {}" +
                    "\nexception:{}", petId, e.getMessage());

            String errorMessage = "Could not update pet with the id '"
                    + petId + "', unexpected error";
            throw new ResourceInternalException(errorMessage);
        }
    }

    @Transactional
    public void deletePet(Long petId) {
        Optional<Pet> petFound = petRepository.findById(petId);

        if (petFound.isEmpty()) {
            String errorMessage = "Pet with id '" + petId + "' not found";
            throw new ResourceConflictException(errorMessage);
        }

        try {
            Pet pet = petFound.get();
            // TODO: first delete PersonPet with pet.id if it exists (or do it via db trigger)

            logger.debug("delete pet with id {}", petId);
            petRepository.deleteById(petId);
        } catch(org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolationException exception when trying to delete pet with id '{}'" +
                    "\nexception:{}", petId, e.getMessage());

            // TODO: implement analysis into the exact cause of DataIntegrityViolationException exception
            String errorMessage = "Could not delete pet with id '"
                    + petId + "'";
            throw new ResourceConflictException(errorMessage);
        } catch(Exception e) {
            logger.error("General exception when trying to delete  pet with id '{}'" +
                    "\nexception:{}", petId, e.getMessage());

            String errorMessage = "Could not delete pet with id '"
                    + petId + "', unexpected error";
            throw new ResourceInternalException(errorMessage);
        }
    }

    @Transactional
    public PetDTO retrievePet(Long petId) {
        Optional<Pet> petFound = petRepository.findById(petId);

        if (petFound.isEmpty()) {
            String errorMessage = "Pet with id '" + petId + "' not found";
            throw new ResourceConflictException(errorMessage);
        }

        try {
            Pet pet = petFound.get();

            logger.debug("retrieve pet with id {}", petId);
            return petMapper.toDTO(pet);
        } catch(org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolationException exception when trying to delete pet with id '{}'" +
                    "\nexception:{}", petId, e.getMessage());

            // TODO: implement analysis into the exact cause of DataIntegrityViolationException exception
            String errorMessage = "Could not retrieve pet with id '"
                    + petId + "'";
            throw new ResourceConflictException(errorMessage);
        } catch(Exception e) {
            logger.error("General exception when trying to delete  pet with id '{}'" +
                    "\nexception:{}", petId, e.getMessage());

            String errorMessage = "Could not retrieve pet with id '"
                    + petId + "', unexpected error";
            throw new ResourceInternalException(errorMessage);
        }
    }

    @Transactional
    public List<PetDTO> retrieveAllPets() {
        List<PetDTO> petDTOs;

        try {
            List<Pet> pets = petRepository.findAll();

            petDTOs = pets.stream().map(pet -> petMapper.toDTO(pet)).collect(Collectors.toList());
        } catch(Exception e) {
            logger.error("General exception when trying to retrieve all pets:\n'{}'" +
                    "\nexception:{}", e.getMessage());

            String errorMessage = "Could not retrieve pets, unexpected error";
            throw new ResourceInternalException(errorMessage);
        }

        return petDTOs;
    }
}
