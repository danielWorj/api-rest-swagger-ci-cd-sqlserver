package bank.michel.compte.model;

import bank.michel.compte.model.Client;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comptes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 34)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeCompte type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCompte statut;

    // Toujours BigDecimal pour l'argent, jamais double/float
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal solde;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal decouvertAutorise;

    @Column(nullable = false, length = 3)
    private String devise;                      // "XAF"

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateOuverture;

    // Verrou optimiste : empêche deux opérations concurrentes d'écraser le solde
    @Version
    private Long version;

    @PrePersist
    void onCreate() { this.dateOuverture = LocalDateTime.now(); }
}