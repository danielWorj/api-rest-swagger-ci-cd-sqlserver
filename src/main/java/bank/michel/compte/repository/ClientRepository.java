package bank.michel.compte.repository;

import bank.michel.compte.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByEmail(String email);
    Optional<Client> findByEmail(String email);
}