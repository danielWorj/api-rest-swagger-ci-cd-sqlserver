package bank.michel.compte.dto;

import bank.michel.compte.model.StatutCompte;
import jakarta.validation.constraints.NotNull;

public record StatutRequest(@NotNull StatutCompte statut) {}