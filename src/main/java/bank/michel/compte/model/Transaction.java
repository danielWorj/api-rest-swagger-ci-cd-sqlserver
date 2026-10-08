package bank.michel.compte.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_tx_compte_date", columnList = "compte_id, dateTransaction DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeTransaction type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal soldeApres;

    private String libelle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateTransaction;

    @PrePersist
    void onCreate() {
        this.dateTransaction = LocalDateTime.now();
        if (this.reference == null) this.reference = UUID.randomUUID();
    }
}