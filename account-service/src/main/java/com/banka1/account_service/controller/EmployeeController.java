package com.banka1.account_service.controller;

import com.banka1.account_service.domain.enums.CurrencyCode;
import com.banka1.account_service.dto.request.*;
import com.banka1.account_service.dto.response.*;
import com.banka1.account_service.service.ClientService;
import com.banka1.account_service.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/employee")
//todo autorizacija

public class EmployeeController {

    private EmployeeService employeeService;
    private ClientService clientService;

    @Operation(summary = "Create checking account")
    @ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PreAuthorize("hasRole('BASIC')")
    @PostMapping("/accounts/checking")
    public ResponseEntity<AccountDetailsResponseDto> createCheckingAccount(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CheckingDto checkingDto) {
        return new ResponseEntity<>(employeeService.createCheckingAccount(jwt, checkingDto), HttpStatus.OK);
    }

    @Operation(summary = "Create FX account")
    @ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PreAuthorize("hasRole('BASIC')")
    @PostMapping("/accounts/fx")
    public ResponseEntity<AccountDetailsResponseDto> createFxAccount(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid FxDto fxDto) {
        return new ResponseEntity<>(employeeService.createFxAccount(jwt, fxDto), HttpStatus.OK);
    }

    @Operation(summary = "Search all accounts")
    @ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PreAuthorize("hasAnyRole('BASIC','SERVICE')")
    @GetMapping("/accounts")
    public ResponseEntity<Page<AccountSearchResponseDto>> searchAllAccounts(@AuthenticationPrincipal Jwt jwt,
                                                                            @RequestParam(required = false) String imeVlasnikaRacuna,
                                                                            @RequestParam(required = false) String prezimeVlasnikaRacuna,
                                                                            @RequestParam(required = false) String accountNumber,
                                                                            @RequestParam(defaultValue = "0") @Min(value = 0) int page,
                                                                            @RequestParam(defaultValue = "10") @Min(value = 1) @Max(value = 100) int size
    ) {
        return new ResponseEntity<>(employeeService.searchAllAccounts(jwt,imeVlasnikaRacuna,prezimeVlasnikaRacuna,accountNumber,page,size), HttpStatus.OK);
    }



    @Operation(summary = "Edit account status")
    @ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Account not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PreAuthorize("hasRole('BASIC')")
    @PutMapping("/accounts/{accountNumber}/status")
    public ResponseEntity<String> editStatus(@AuthenticationPrincipal Jwt jwt, @PathVariable String accountNumber, @RequestBody @Valid EditStatus editStatus) {
        return new ResponseEntity<>(clientService.editStatus(jwt, accountNumber, editStatus), HttpStatus.OK);
    }

    @Operation(summary = "Get account details by account number (employee access)")
    @PreAuthorize("hasRole('BASIC')")
    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<AccountDetailsResponseDto> getAccountDetails(@AuthenticationPrincipal Jwt jwt,
                                                                       @PathVariable String accountNumber) {
        return new ResponseEntity<>(employeeService.getAccountDetails(accountNumber), HttpStatus.OK);
    }

    @Operation(summary = "Get all accounts for a specific client")
    @PreAuthorize("hasRole('BASIC')")
    @GetMapping("/accounts/client/{clientId}")
    public ResponseEntity<Page<AccountDetailsResponseDto>> getClientAccounts(@AuthenticationPrincipal Jwt jwt,
                                                                             @PathVariable Long clientId,
                                                                             @RequestParam(defaultValue = "0") @Min(0) int page,
                                                                             @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return new ResponseEntity<>(employeeService.getClientAccounts(clientId, page, size), HttpStatus.OK);
    }

    // Cards are managed by the Card Service — this endpoint is intentionally disabled here
//    @Operation(summary = "Get cards for a specific account (employee access)")
//    @PreAuthorize("hasRole('BASIC')")
//    @GetMapping("/accounts/{accountNumber}/cards")
//    public ResponseEntity<Page<CardResponseDto>> getAccountCards(@AuthenticationPrincipal Jwt jwt,
//                                                                 @PathVariable String accountNumber,
//                                                                 @RequestParam(defaultValue = "0") @Min(0) int page,
//                                                                 @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
//        return new ResponseEntity<>(employeeService.getAccountCards(accountNumber, page, size), HttpStatus.OK);
//    }

    @Operation(summary = "Get all bank internal accounts")
    @PreAuthorize("hasRole('BASIC')")
    @GetMapping("/accounts/bank")
    public ResponseEntity<List<AccountDetailsResponseDto>> getBankAccounts(@AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(employeeService.getBankAccounts(), HttpStatus.OK);
    }

    @Operation(summary = "Get bank internal account for a specific currency")
    @PreAuthorize("hasRole('BASIC')")
    @GetMapping("/accounts/bank/{currency}")
    public ResponseEntity<AccountDetailsResponseDto> getBankAccountByCurrency(@AuthenticationPrincipal Jwt jwt,
                                                                               @PathVariable CurrencyCode currency) {
        return new ResponseEntity<>(employeeService.getBankAccountByCurrency(currency), HttpStatus.OK);
    }

    @Operation(summary = "Get company details")
    @PreAuthorize("hasRole('BASIC')")
    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyResponseDto> getCompany(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return new ResponseEntity<>(employeeService.getCompany(id), HttpStatus.OK);
    }

    @Operation(summary = "Update company details")
    @PreAuthorize("hasRole('BASIC')")
    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyResponseDto> updateCompany(@AuthenticationPrincipal Jwt jwt,
                                                            @PathVariable Long id,
                                                            @RequestBody @Valid UpdateCompanyDto dto) {
        return new ResponseEntity<>(employeeService.updateCompany(id, dto), HttpStatus.OK);
    }
}
