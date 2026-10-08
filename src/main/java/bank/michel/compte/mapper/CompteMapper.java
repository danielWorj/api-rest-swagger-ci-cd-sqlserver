package bank.michel.compte.mapper;

import bank.michel.compte.dto.CompteResponse;
import bank.michel.compte.model.Compte;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompteMapper {
    @Mapping(source = "client.id", target = "clientId")
    CompteResponse toResponse(Compte compte);
}
