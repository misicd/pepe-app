package org.dmship.mapping;

import org.dmship.dto.PetDTO;
import org.dmship.model.Pet;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        config = PersonsPetsMappingConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personPet", ignore = true)
    Pet toEntity(PetDTO personDTO);

    PetDTO toDTO(Pet person);
}
