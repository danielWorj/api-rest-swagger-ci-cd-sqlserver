package bank.michel.compte.dto;

import bank.michel.compte.model.StatutCompte;
import bank.michel.compte.model.TypeCompte;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompteResponse(Long id, String numero, TypeCompte type, StatutCompte statut,
                             BigDecimal solde, String devise, BigDecimal decouvertAutorise,
                             Long clientId, LocalDateTime dateOuverture) {}
