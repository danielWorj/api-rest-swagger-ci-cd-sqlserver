package bank.michel.compte.mapper;

import bank.michel.compte.dto.TransactionResponse;
import bank.michel.compte.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "compte.numero", target = "numeroCompte")
    TransactionResponse toResponse(Transaction transaction);
}