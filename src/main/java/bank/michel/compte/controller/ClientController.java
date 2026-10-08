package bank.michel.compte.controller;

import bank.michel.compte.dto.ClientRequest;
import bank.michel.compte.dto.ClientResponse;
import bank.michel.compte.dto.CompteResponse;
import bank.michel.compte.service.ClientService;
import bank.michel.compte.service.CompteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Gestion des clients")
public class ClientController {

    private final ClientService clientService;
    private final CompteService compteService;

    @GetMapping
    @Operation(summary = "Lister les clients (paginé)")
    public Page<ClientResponse> lister(@ParameterObject Pageable pageable) {
        return clientService.lister(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    public ClientResponse get(@PathVariable Long id) {
        return clientService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un client")
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    public ClientResponse creer(@Valid @RequestBody ClientRequest request) {
        return clientService.creer(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un client")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    public ClientResponse modifier(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return clientService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un client (sans compte actif)")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    @ApiResponse(responseCode = "409", description = "Le client possède des comptes non clôturés")
    public void supprimer(@PathVariable Long id) {
        clientService.supprimer(id);
    }

    @GetMapping("/{id}/comptes")
    @Operation(summary = "Lister les comptes d'un client")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    public List<CompteResponse> comptesDuClient(@PathVariable Long id) {
        return compteService.comptesDuClient(id);
    }
}