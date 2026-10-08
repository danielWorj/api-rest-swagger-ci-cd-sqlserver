package bank.michel.compte.controller;

import bank.michel.compte.dto.CompteRequest;
import bank.michel.compte.dto.CompteResponse;
import bank.michel.compte.dto.StatutRequest;
import bank.michel.compte.service.CompteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comptes")
@RequiredArgsConstructor
@Tag(name = "Comptes", description = "Ouverture et gestion des comptes bancaires")
public class CompteController {

    private final CompteService compteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ouvrir un compte")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    public CompteResponse ouvrir(@Valid @RequestBody CompteRequest request) {
        return compteService.ouvrir(request);
    }

    @GetMapping("/{numero}")
    @Operation(summary = "Consulter un compte par son numéro")
    @ApiResponse(responseCode = "404", description = "Compte introuvable")
    public CompteResponse get(@PathVariable String numero) {
        return compteService.get(numero);
    }

    @PatchMapping("/{numero}/statut")
    @Operation(summary = "Changer le statut d'un compte (bloquer, clôturer...)")
    @ApiResponse(responseCode = "404", description = "Compte introuvable")
    @ApiResponse(responseCode = "409", description = "Changement de statut interdit")
    public CompteResponse changerStatut(@PathVariable String numero,
                                        @Valid @RequestBody StatutRequest request) {
        return compteService.changerStatut(numero, request.statut());
    }
}