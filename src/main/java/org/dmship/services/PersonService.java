package org.dmship.services;

import lombok.RequiredArgsConstructor;
import org.dmship.dto.PersonDTO;
import org.dmship.dto.PersonUpdateDTO;
import org.dmship.exceptions.*;
import org.dmship.mapping.PersonMapper;
import org.dmship.mapping.PersonUpdateMapper;
import org.dmship.model.*;
import org.dmship.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final PersonMapper personMapper;
    private final PersonUpdateMapper personUpdateMapper;
    private final PersonRepository personRepository;

    public Optional<PersonDTO> findById(Long personId) {
        Optional<Person> person = personRepository.findById(personId);

        if (person.isEmpty()) {
            return Optional.empty();
        }

        PersonDTO personDTO = personMapper.toDTO(person.get());
        return Optional.of(personDTO);
    }

    @Transactional
    public Long createPerson(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);

        try {
            logger.debug("create person before save:\n{}", person);
            person = personRepository.saveAndFlush(person);
            logger.debug("created person:\n{}", person);
        } catch(org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolationException exception when trying to create person:\n{}" +
                    "\nexception:{}", person, e.getMessage());

            // TODO: implement analysis into the exact cause of DataIntegrityViolationException exception
            String errorMessage = "Could not create person, another person with the same name '"
                    + person.getFullName() + "' already exists";
            throw new ResourceConflictException(errorMessage);
        } catch(Exception e) {
            logger.error("General exception when trying to create person:\n{}" +
                    "\nexception:{}", person, e.getMessage());

            String errorMessage = "Could not create person with the name '"
                    + person.getFullName() + "', unexpected error";
            throw new ResourceInternalException(errorMessage);
        }

        return person.getId();
    }

    @Transactional
    public void updatePerson(Long personId, PersonUpdateDTO personUpdateDTO) {
        Optional<Person> personFound = personRepository.findById(personId);

        if (personFound.isEmpty()) {
            String errorMessage = "Person with id '" + personId + "' not found";
            throw new ResourceConflictException(errorMessage);
        }

        Person person = personFound.get();

        try {
            PersonUpdate personUpdate = personUpdateMapper.toEntity(personUpdateDTO);
            person.update(personUpdate);

            logger.debug("update person before save:\n{}", person);
            Person personUpdated = personRepository.saveAndFlush(person);
            logger.debug("update person after save:\n{}", personUpdated);
        } catch(org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolationException exception when trying to update person:\n{}" +
                    "\nexception:{}", person, e.getMessage());

            // TODO: implement analysis into the exact cause of DataIntegrityViolationException exception
            String errorMessage = "Could not update person, another person with the name '"
                    + person.getFullName() + "' equal to updated name already exists";
            throw new ResourceConflictException(errorMessage);
        } catch(Exception e) {
            logger.error("General exception when trying to update person with id {}" +
                    "\nexception:{}", personId, e.getMessage());

            String errorMessage = "Could not update person with the id '"
                    + personId + "', unexpected error";
            throw new ResourceInternalException(errorMessage);
        }
    }

    @Transactional
    public PersonDTO retrievePerson(Long personId) {
        Optional<Person> personFound = personRepository.findById(personId);

        if (personFound.isEmpty()) {
            String errorMessage = "Person with id '" + personId + "' not found";
            throw new ResourceConflictException(errorMessage);
        }

        try {
            logger.debug("retrieved person with id {}", personId);
            return personMapper.toDTO(personFound.get());
        } catch(Exception e) {
            logger.error("General exception when trying to retrieve person with id '{}'" +
                    "\nexception:{}", personId, e.getMessage());

            String errorMessage = "Could not retrieve person with id '"
                    + personId + "', unexpected error";
            throw new ResourceInternalException(errorMessage);
        }
    }

    @Transactional
    public List<PersonDTO> retrievePersons(PersonSearchCriteria personSearchCriteria) {
        List<PersonDTO> personDTOs;

        try {
            logger.debug("retrieve persons using search criteria:\n{}", personSearchCriteria);

            Specification<Person> personSpecification =
                    PersonSpecificationBuilder.createPersonSpecification(personSearchCriteria);

            List<Person> persons = personRepository.findAll(personSpecification);

            personDTOs = persons.stream().map(person -> personMapper.toDTO(person)).collect(Collectors.toList());
        } catch(Exception e) {
            logger.error("General exception when trying to retrieve persons using search criteria:\n'{}'" +
                    "\nexception:{}", personSearchCriteria, e.getMessage());

            String errorMessage = "Could not retrieve persons, unexpected error";
            throw new ResourceInternalException(errorMessage);
        }

        return personDTOs;
    }
}
