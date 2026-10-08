package bank.michel.compte.repository;

import bank.michel.compte.model.Compte;
import bank.michel.compte.model.StatutCompte;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompteRepository extends JpaRepository<Compte, Long> {
    Optional<Compte> findByNumero(String numero);
    List<Compte> findByClientId(Long clientId);
    boolean existsByClientIdAndStatutNot(Long clientId, StatutCompte statut);

    // Verrou pessimiste pour les virements (évite les conditions de course)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Compte c where c.numero = :numero")
    Optional<Compte> findByNumeroForUpdate(@Param("numero") String numero);
}
