package bank.michel.compte.model;

import bank.michel.compte.model.Compte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients", indexes = @Index(name = "idx_client_email", columnList = "email", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // IDENTITY = natif SQL Server
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 30)
    private String telephone;

    private LocalDate dateNaissance;

    private String adresse;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Compte> comptes = new ArrayList<>();

    @PrePersist
    void onCreate() { this.dateCreation = LocalDateTime.now(); }
}