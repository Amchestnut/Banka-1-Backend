package com.banka1.account_service.service;

import com.banka1.account_service.domain.enums.CurrencyCode;
import com.banka1.account_service.dto.request.CheckingDto;
import com.banka1.account_service.dto.request.FxDto;
import com.banka1.account_service.dto.request.UpdateCardDto;
import com.banka1.account_service.dto.request.UpdateCompanyDto;
import com.banka1.account_service.dto.response.AccountDetailsResponseDto;
import com.banka1.account_service.dto.response.AccountSearchResponseDto;
import com.banka1.account_service.dto.response.CardResponseDto;
import com.banka1.account_service.dto.response.CompanyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

/**
 * Servis koji implementira poslovnu logiku dostupnu zaposlenima banke.
 * Pokriva kreiranje racuna, pretragu racuna i upravljanje karticama.
 */
public interface EmployeeService {

    AccountDetailsResponseDto createFxAccount(Jwt jwt, FxDto fxDto);

    AccountDetailsResponseDto createCheckingAccount(Jwt jwt, CheckingDto checkingDto);

    Page<AccountSearchResponseDto> searchAllAccounts(Jwt jwt, String imeVlasnikaRacuna, String prezimeVlasnikaRacuna, String accountNumber, int page, int size);

    String updateCard(Jwt jwt, Long id, UpdateCardDto updateCardDto);

    List<AccountDetailsResponseDto> getBankAccounts();

    AccountDetailsResponseDto getBankAccountByCurrency(CurrencyCode currencyCode);

    AccountDetailsResponseDto getAccountDetails(String accountNumber);

    Page<AccountDetailsResponseDto> getClientAccounts(Long clientId, int page, int size);

    // Cards are managed by the Card Service
//    Page<CardResponseDto> getAccountCards(String accountNumber, int page, int size);

    CompanyResponseDto getCompany(Long id);

    CompanyResponseDto updateCompany(Long id, UpdateCompanyDto dto);
}
