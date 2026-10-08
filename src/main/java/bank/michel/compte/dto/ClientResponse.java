package bank.michel.compte.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientResponse(Long id, String nom, String prenom, String email,
                             String telephone, LocalDate dateNaissance,
                             String adresse, LocalDateTime dateCreation) {}