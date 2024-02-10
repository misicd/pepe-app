package org.dmship.repository;

import org.dmship.model.PersonPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PersonPetRepository extends JpaRepository<PersonPet, Long> {

    Optional<PersonPet> findByPersonIdAndPetId(Long personId, Long petId);

    List<PersonPet> findByPersonId(Long personId);
}