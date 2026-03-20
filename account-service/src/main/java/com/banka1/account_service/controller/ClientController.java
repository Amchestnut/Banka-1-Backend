package com.banka1.account_service.controller;

import com.banka1.account_service.dto.request.*;
import com.banka1.account_service.dto.response.*;
import com.banka1.account_service.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@AllArgsConstructor
@PreAuthorize("hasRole('CLIENT_BASIC')")
//todo dodati autorizaciju na endpointe
public class ClientController {
    private ClientService clientService;
    @Operation(summary = "Create a new payment")
    @PostMapping("/payments")
    public ResponseEntity<String> newPayment(@AuthenticationPrincipal Jwt jwt,@RequestBody @Valid NewPaymentDto newPaymentDto) {
        return new ResponseEntity<>(clientService.newPayment(jwt,newPaymentDto), HttpStatus.OK);
    }
    @Operation(summary = "Approve a transaction")
    @PostMapping("/transactions/{id}/approve")
    public ResponseEntity<String> approveTransaction(@AuthenticationPrincipal Jwt jwt,@PathVariable Long id,@RequestBody @Valid ApproveDto approveDto) {
        return new ResponseEntity<>(clientService.approveTransaction(jwt,id,approveDto), HttpStatus.OK);
    }
    //todo mozda detalji i find uopste ne moraju da se razlikuju po pitanju toga sta se vraca
    @Operation(summary = "Get my accounts")
    @GetMapping("/accounts")
    public ResponseEntity<Page<AccountResponseDto>> findMyAccounts(@AuthenticationPrincipal Jwt jwt,
                                                                   @RequestParam(defaultValue = "0") @Min(value = 0) int page,
                                                                   @RequestParam(defaultValue = "10") @Min(value = 1) @Max(value = 100) int size) {
        return new ResponseEntity<>(clientService.findMyAccounts(jwt,page,size), HttpStatus.OK);
    }


    @Operation(summary = "Get account transactions")
    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity<Page<TransactionResponseDto>> findAllTransactions(@AuthenticationPrincipal Jwt jwt,
                                                                            @PathVariable Long id,
                                                                            @RequestParam(defaultValue = "0") @Min(value = 0) int page,
                                                                            @RequestParam(defaultValue = "10") @Min(value = 1) @Max(value = 100) int size) {
        return new ResponseEntity<>(clientService.findAllTransactions(jwt,id,page,size), HttpStatus.OK);
    }

    @Operation(summary = "Edit account name")
    @PatchMapping("/accounts/{id}/name")
    public ResponseEntity<String> editAccountName(@AuthenticationPrincipal Jwt jwt,@PathVariable Long id,@RequestBody @Valid EditAccountNameDto editAccountNameDto)
    {
        return new ResponseEntity<>(clientService.editAccountName(jwt,id,editAccountNameDto), HttpStatus.OK);
    }
    //todo samo vlasnik racuna, znaci nema autorizacije vrv samo menjas za sebe a ne exposujes endpoint da neko moze za nekog drugog
    //todo dodaj verifikaciju preko mobilnog
    @Operation(summary = "Edit account transaction limit")
    @PatchMapping("/accounts/{id}/limit")
    public ResponseEntity<String> editAccountLimit(@AuthenticationPrincipal Jwt jwt,@PathVariable Long id,@RequestBody @Valid EditAccountLimitDto editAccountLimitDto)
    {
        return new ResponseEntity<>(clientService.editAccountLimit(jwt,id,editAccountLimitDto), HttpStatus.OK);
    }
    @Operation(summary = "Get account details")
    @GetMapping("/accounts/{id}")
    public ResponseEntity<AccountDetailsResponseDto> getDetails (@AuthenticationPrincipal Jwt jwt,@PathVariable Long id)
    {
        return new ResponseEntity<>(clientService.getDetails(jwt,id), HttpStatus.OK);
    }
    @Operation(summary = "Get account cards")
    @GetMapping("/accounts/{id}/cards")
    public ResponseEntity<Page<CardResponseDto>> findAllCards(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable Long id,
                                                              @RequestParam(defaultValue = "0") @Min(value = 0) int page,
                                                              @RequestParam(defaultValue = "10") @Min(value = 1) @Max(value = 100) int size) {
        return new ResponseEntity<>(clientService.findAllCards(jwt,id,page,size), HttpStatus.OK);
    }



}
