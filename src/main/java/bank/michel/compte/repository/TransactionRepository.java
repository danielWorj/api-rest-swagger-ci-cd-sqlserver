package bank.michel.compte.repository;

import bank.michel.compte.model.Transaction;
import bank.michel.compte.model.TypeTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        select t from Transaction t
        where t.compte.numero = :numero
          and (:type is null or t.type = :type)
          and (:debut is null or t.dateTransaction >= :debut)
          and (:fin is null or t.dateTransaction < :fin)
        """)
    Page<Transaction> rechercher(@Param("numero") String numero,
                                 @Param("type") TypeTransaction type,
                                 @Param("debut") LocalDateTime debut,
                                 @Param("fin") LocalDateTime fin,
                                 Pageable pageable);
}