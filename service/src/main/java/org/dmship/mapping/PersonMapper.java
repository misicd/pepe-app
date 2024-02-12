package org.dmship.mapping;

import org.dmship.dto.PersonDTO;
import org.dmship.model.Person;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        config = PersonsPetsMappingConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personPets", ignore = true)
    Person toEntity(PersonDTO personDTO);

    PersonDTO toDTO(Person person);
}
