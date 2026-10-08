package bank.michel.compte.service;

import bank.michel.compte.dto.OperationRequest;
import bank.michel.compte.dto.TransactionResponse;
import bank.michel.compte.dto.VirementRequest;
import bank.michel.compte.exception.BusinessException;
import bank.michel.compte.exception.InsufficientFundsException;
import bank.michel.compte.exception.ResourceNotFoundException;
import bank.michel.compte.mapper.TransactionMapper;
import bank.michel.compte.model.Compte;
import bank.michel.compte.model.StatutCompte;
import bank.michel.compte.model.Transaction;
import bank.michel.compte.model.TypeTransaction;
import bank.michel.compte.repository.CompteRepository;
import bank.michel.compte.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper mapper;

    @Transactional
    public TransactionResponse depot(String numero, OperationRequest req) {
        Compte compte = getCompteActifPourMiseAJour(numero);
        compte.setSolde(compte.getSolde().add(req.montant()));
        return mapper.toResponse(enregistrer(compte, TypeTransaction.DEPOT, req.montant(), req.libelle()));
    }

    @Transactional
    public TransactionResponse retrait(String numero, OperationRequest req) {
        Compte compte = getCompteActifPourMiseAJour(numero);
        BigDecimal nouveauSolde = compte.getSolde().subtract(req.montant());
        if (nouveauSolde.compareTo(compte.getDecouvertAutorise().negate()) < 0) {
            throw new InsufficientFundsException("Solde insuffisant sur le compte " + numero);
        }
        compte.setSolde(nouveauSolde);
        return mapper.toResponse(enregistrer(compte, TypeTransaction.RETRAIT, req.montant(), req.libelle()));
    }

    @Transactional
    public List<TransactionResponse> virement(VirementRequest req) {
        if (req.compteSource().equals(req.compteDestination())) {
            throw new BusinessException("Les comptes source et destination doivent être différents");
        }
        // Verrouillage dans un ordre fixe pour éviter les deadlocks
        List<String> ordre = Stream.of(req.compteSource(), req.compteDestination()).sorted().toList();
        Map<String, Compte> comptes = new HashMap<>();
        ordre.forEach(n -> comptes.put(n, getCompteActifPourMiseAJour(n)));

        Compte source = comptes.get(req.compteSource());
        Compte dest = comptes.get(req.compteDestination());

        BigDecimal nouveauSolde = source.getSolde().subtract(req.montant());
        if (nouveauSolde.compareTo(source.getDecouvertAutorise().negate()) < 0) {
            throw new InsufficientFundsException("Solde insuffisant sur le compte " + source.getNumero());
        }
        source.setSolde(nouveauSolde);
        dest.setSolde(dest.getSolde().add(req.montant()));

        return List.of(
                mapper.toResponse(enregistrer(source, TypeTransaction.VIREMENT_DEBIT, req.montant(), req.libelle())),
                mapper.toResponse(enregistrer(dest, TypeTransaction.VIREMENT_CREDIT, req.montant(), req.libelle()))
        );
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> historique(String numero, TypeTransaction type,
                                                LocalDate debut, LocalDate fin, Pageable pageable) {
        if (!compteRepository.findByNumero(numero).isPresent())
            throw new ResourceNotFoundException("Compte introuvable : " + numero);
        LocalDateTime d = debut != null ? debut.atStartOfDay() : null;
        LocalDateTime f = fin != null ? fin.plusDays(1).atStartOfDay() : null;
        return transactionRepository.rechercher(numero, type, d, f, pageable).map(mapper::toResponse);
    }

    // ---- privé ----
    private Compte getCompteActifPourMiseAJour(String numero) {
        Compte c = compteRepository.findByNumeroForUpdate(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + numero));
        if (c.getStatut() != StatutCompte.ACTIF)
            throw new BusinessException("Le compte " + numero + " n'est pas actif");
        return c;
    }

    private Transaction enregistrer(Compte compte, TypeTransaction type, BigDecimal montant, String libelle) {
        return transactionRepository.save(Transaction.builder()
                .compte(compte).type(type).montant(montant)
                .soldeApres(compte.getSolde()).libelle(libelle).build());
    }
}