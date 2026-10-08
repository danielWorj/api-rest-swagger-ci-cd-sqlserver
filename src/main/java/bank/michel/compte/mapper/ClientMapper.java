package bank.michel.compte.mapper;

import bank.michel.compte.dto.ClientRequest;
import bank.michel.compte.dto.ClientResponse;
import bank.michel.compte.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toEntity(ClientRequest request);
    ClientResponse toResponse(Client client);
    void update(ClientRequest request, @MappingTarget Client client);
}