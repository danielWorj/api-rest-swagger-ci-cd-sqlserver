package bank.michel.compte.dto;

import bank.michel.compte.model.TypeCompte;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CompteRequest(
        @NotNull Long clientId,
        @NotNull TypeCompte type,
        @PositiveOrZero BigDecimal depotInitial,
        @PositiveOrZero BigDecimal decouvertAutorise) {}