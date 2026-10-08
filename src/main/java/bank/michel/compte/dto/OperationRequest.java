package bank.michel.compte.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OperationRequest(
        @NotNull @DecimalMin(value = "0.01") BigDecimal montant,
        String libelle) {}
