package org.dmship.repository;

import jakarta.annotation.Nullable;
import org.dmship.model.Person;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    Optional<Person> findByFirstNameAndLastName(String firstName, String lastName);

    List<Person> findAll(@Nullable Specification<Person> personSpecification);
}