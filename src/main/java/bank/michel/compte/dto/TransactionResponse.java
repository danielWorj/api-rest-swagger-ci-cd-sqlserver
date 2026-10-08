package bank.michel.compte.dto;

import bank.michel.compte.model.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(Long id, UUID reference, TypeTransaction type,
                                  BigDecimal montant, BigDecimal soldeApres, String libelle,
                                  String numeroCompte, LocalDateTime dateTransaction) {}
