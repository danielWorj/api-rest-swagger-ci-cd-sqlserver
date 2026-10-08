package bank.michel.compte.service;

import bank.michel.compte.dto.CompteRequest;
import bank.michel.compte.dto.CompteResponse;
import bank.michel.compte.exception.BusinessException;
import bank.michel.compte.exception.ResourceNotFoundException;
import bank.michel.compte.mapper.CompteMapper;
import bank.michel.compte.model.*;
import bank.michel.compte.repository.ClientRepository;
import bank.michel.compte.repository.CompteRepository;
import bank.michel.compte.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CompteService {

    private static final String DEVISE_PAR_DEFAUT = "XAF";

    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;
    private final CompteMapper mapper;

    @Transactional
    public CompteResponse ouvrir(CompteRequest req) {
        Client client = clientRepository.findById(req.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable : " + req.clientId()));

        BigDecimal decouvert = req.decouvertAutorise() != null ? req.decouvertAutorise() : BigDecimal.ZERO;
        if (req.type() == TypeCompte.EPARGNE && decouvert.signum() > 0) {
            throw new BusinessException("Un compte épargne ne peut pas avoir de découvert autorisé");
        }

        BigDecimal depotInitial = req.depotInitial() != null ? req.depotInitial() : BigDecimal.ZERO;

        Compte compte = compteRepository.save(Compte.builder()
                .numero(genererNumero())
                .type(req.type())
                .statut(StatutCompte.ACTIF)
                .solde(depotInitial)
                .decouvertAutorise(decouvert)
                .devise(DEVISE_PAR_DEFAUT)
                .client(client)
                .build());

        // Dépôt initial : tracé comme une transaction DEPOT
        if (depotInitial.signum() > 0) {
            transactionRepository.save(Transaction.builder()
                    .compte(compte)
                    .type(TypeTransaction.DEPOT)
                    .montant(depotInitial)
                    .soldeApres(compte.getSolde())
                    .libelle("Dépôt initial")
                    .build());
        }

        return mapper.toResponse(compte);
    }

    @Transactional(readOnly = true)
    public CompteResponse get(String numero) {
        return mapper.toResponse(getOuErreur(numero));
    }

    @Transactional(readOnly = true)
    public List<CompteResponse> listerParClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client introuvable : " + clientId);
        }
        return compteRepository.findByClientId(clientId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public CompteResponse changerStatut(String numero, StatutCompte nouveauStatut) {
        // Verrou pessimiste : évite de changer le statut pendant une opération en cours
        Compte compte = compteRepository.findByNumeroForUpdate(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + numero));

        if (compte.getStatut() == StatutCompte.CLOTURE) {
            throw new BusinessException("Le compte " + numero + " est clôturé et ne peut plus être modifié");
        }
        if (compte.getStatut() == nouveauStatut) {
            throw new BusinessException("Le compte " + numero + " est déjà au statut " + nouveauStatut);
        }
        if (nouveauStatut == StatutCompte.CLOTURE && compte.getSolde().signum() != 0) {
            throw new BusinessException("Clôture impossible : le solde du compte doit être nul");
        }

        compte.setStatut(nouveauStatut);
        return mapper.toResponse(compte);
    }

    // ---- privé ----
    private Compte getOuErreur(String numero) {
        return compteRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable : " + numero));
    }

    private String genererNumero() {
        String numero;
        do {
            numero = "CM21" + String.format("%016d", ThreadLocalRandom.current().nextLong(1_0000_0000_0000_000L));
        } while (compteRepository.findByNumero(numero).isPresent());
        return numero;
    }
}