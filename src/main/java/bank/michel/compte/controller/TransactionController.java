package bank.michel.compte.controller;

import bank.michel.compte.dto.OperationRequest;
import bank.michel.compte.dto.TransactionResponse;
import bank.michel.compte.dto.VirementRequest;
import bank.michel.compte.model.TypeTransaction;
import bank.michel.compte.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Dépôts, retraits, virements et historique")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/comptes/{numero}/depot")
    @Operation(summary = "Effectuer un dépôt")
    @ApiResponse(responseCode = "404", description = "Compte introuvable")
    @ApiResponse(responseCode = "409", description = "Compte non actif")
    public TransactionResponse depot(@PathVariable String numero,
                                     @Valid @RequestBody OperationRequest request) {
        return transactionService.depot(numero, request);
    }

    @PostMapping("/comptes/{numero}/retrait")
    @Operation(summary = "Effectuer un retrait")
    @ApiResponse(responseCode = "404", description = "Compte introuvable")
    @ApiResponse(responseCode = "422", description = "Solde insuffisant")
    public TransactionResponse retrait(@PathVariable String numero,
                                       @Valid @RequestBody OperationRequest request) {
        return transactionService.retrait(numero, request);
    }

    @PostMapping("/virements")
    @Operation(summary = "Effectuer un virement entre deux comptes",
            description = "Retourne les deux écritures : débit puis crédit")
    @ApiResponse(responseCode = "404", description = "Compte introuvable")
    @ApiResponse(responseCode = "422", description = "Solde insuffisant")
    public List<TransactionResponse> virement(@Valid @RequestBody VirementRequest request) {
        return transactionService.virement(request);
    }

    @GetMapping("/comptes/{numero}/transactions")
    @Operation(summary = "Historique des transactions d'un compte (paginé)")
    @ApiResponse(responseCode = "404", description = "Compte introuvable")
    public Page<TransactionResponse> historique(
            @PathVariable String numero,
            @RequestParam(required = false) TypeTransaction type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @ParameterObject @PageableDefault(size = 20, sort = "dateTransaction",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return transactionService.historique(numero, type, dateDebut, dateFin, pageable);
    }
}