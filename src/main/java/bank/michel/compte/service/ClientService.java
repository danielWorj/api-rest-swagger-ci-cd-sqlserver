package bank.michel.compte.service;

import bank.michel.compte.dto.ClientRequest;
import bank.michel.compte.dto.ClientResponse;
import bank.michel.compte.exception.BusinessException;
import bank.michel.compte.exception.ResourceNotFoundException;
import bank.michel.compte.mapper.ClientMapper;
import bank.michel.compte.model.Client;
import bank.michel.compte.model.StatutCompte;
import bank.michel.compte.repository.ClientRepository;
import bank.michel.compte.repository.CompteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final CompteRepository compteRepository;
    private final ClientMapper mapper;

    @Transactional(readOnly = true)
    public Page<ClientResponse> lister(Pageable pageable) {
        return clientRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClientResponse get(Long id) {
        return mapper.toResponse(getOuErreur(id));
    }

    @Transactional
    public ClientResponse creer(ClientRequest request) {
        if (clientRepository.existsByEmail(request.email())) {
            throw new BusinessException("Un client avec l'email " + request.email() + " existe déjà");
        }
        Client client = mapper.toEntity(request);
        return mapper.toResponse(clientRepository.save(client));
    }

    @Transactional
    public ClientResponse modifier(Long id, ClientRequest request) {
        Client client = getOuErreur(id);

        // L'email doit rester unique, sauf s'il appartient déjà à ce client
        clientRepository.findByEmail(request.email())
                .filter(autre -> !autre.getId().equals(id))
                .ifPresent(autre -> {
                    throw new BusinessException("Un client avec l'email " + request.email() + " existe déjà");
                });

        mapper.update(request, client);
        return mapper.toResponse(clientRepository.save(client));
    }

    @Transactional
    public void supprimer(Long id) {
        Client client = getOuErreur(id);

        // Suppression refusée si le client possède au moins un compte non clôturé
        if (compteRepository.existsByClientIdAndStatutNot(id, StatutCompte.CLOTURE)) {
            throw new BusinessException(
                    "Suppression impossible : le client possède des comptes non clôturés");
        }
        clientRepository.delete(client);
    }

    // ---- privé ----
    private Client getOuErreur(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable : " + id));
    }
}