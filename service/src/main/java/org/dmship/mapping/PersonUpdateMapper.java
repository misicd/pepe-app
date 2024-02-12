package org.dmship.mapping;

import org.dmship.dto.PersonUpdateDTO;
import org.dmship.model.Person;
import org.dmship.model.PersonUpdate;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        config = PersonsPetsMappingConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PersonUpdateMapper {

    PersonUpdate toEntity(PersonUpdateDTO personUpdateDTO);
}
