package bank.michel.compte.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record VirementRequest(
        @NotBlank String compteSource,
        @NotBlank String compteDestination,
        @NotNull @DecimalMin("0.01") BigDecimal montant,
        String libelle) {}