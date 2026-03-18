package com.banka1.account_service.service.implementation;

import com.banka1.account_service.domain.*;
import com.banka1.account_service.domain.enums.AccountOwnershipType;
import com.banka1.account_service.domain.enums.CurrencyCode;
import com.banka1.account_service.domain.enums.Status;
import com.banka1.account_service.dto.request.CheckingDto;
import com.banka1.account_service.dto.request.FxDto;
import com.banka1.account_service.dto.request.UpdateCardDto;
import com.banka1.account_service.dto.response.AccountSearchResponseDto;
import com.banka1.account_service.repository.AccountRepository;
import com.banka1.account_service.repository.CompanyRepository;
import com.banka1.account_service.repository.CurrencyRepository;
import com.banka1.account_service.repository.SifraDelatnostiRepository;
import com.banka1.account_service.rest_client.ClientServiceClient;
import com.banka1.account_service.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;


@Service
public class EmployeeServiceImplementation implements EmployeeService {
    private final Random random;
    private final ClientServiceClient clientServiceClient;
    private final AccountRepository accountRepository;
    @Value("${banka.security.id}")
    private String appPropertiesId;
    private final CurrencyRepository currencyRepository;
    private final SifraDelatnostiRepository sifraDelatnostiRepository;
    private final CompanyRepository companyRepository;

    public EmployeeServiceImplementation(@Value("${random.seed}") Long seed, ClientServiceClient clientServiceClient, CurrencyRepository currencyRepository, SifraDelatnostiRepository sifraDelatnostiRepository, CompanyRepository companyRepository, AccountRepository accountRepository)
    {
        this.clientServiceClient=clientServiceClient;
        this.random=new Random(seed);
        this.currencyRepository=currencyRepository;
        this.sifraDelatnostiRepository=sifraDelatnostiRepository;
        this.companyRepository=companyRepository;
        this.accountRepository = accountRepository;
    }
    @Transactional
    @Override
    public String createFxAccount(Jwt jwt, FxDto fxDto) {
        if(fxDto.getCurrencyCode() == CurrencyCode.RSD)
            throw new IllegalArgumentException("Ne moze RSD");
        if(fxDto.getIdVlasnika()==null && (fxDto.getJmbg()==null||fxDto.getJmbg().isBlank()))
            throw new IllegalArgumentException("Unesi id ili jmbg");
        if(fxDto.getFirma()==null && fxDto.getTipRacuna() == AccountOwnershipType.BUSINESS || fxDto.getFirma()!=null && fxDto.getTipRacuna() == AccountOwnershipType.PERSONAL)
            throw new IllegalArgumentException("Pogresan tip racuna");

        Currency currency=currencyRepository.findByOznaka(fxDto.getCurrencyCode()).orElse(null);
        if(currency==null )
                throw new IllegalArgumentException("Nisu unete valute");
        if(currency.getStatus() == Status.INACTIVE)
            throw new IllegalArgumentException("Deaktivirana valuta");
        Company company=null;
        if(fxDto.getFirma()!=null) {
            SifraDelatnosti sifraDelatnosti = sifraDelatnostiRepository.findByOznaka(fxDto.getFirma().getSifraDelatnosti()).orElse(null);
            if (sifraDelatnosti == null)
                throw new IllegalArgumentException("Nije uneta sifra delatnosti");
            company = new Company(fxDto.getFirma().getNaziv(), fxDto.getFirma().getMaticniBroj(), fxDto.getFirma().getPoreskiBroj(), sifraDelatnosti, fxDto.getFirma().getAdresa(), fxDto.getFirma().getVlasnik());
            company = companyRepository.save(company);
        }
        Long id=null;
        if(fxDto.getIdVlasnika()!=null)
            id=fxDto.getIdVlasnika();
        else
        {
            id=clientServiceClient.getUser(fxDto.getJmbg()).getId();
        }
        StringBuilder stringBuilder=new StringBuilder();
        boolean exit=true;
        while (exit) {
            for (int i = 0; i < 9; i++) {
                stringBuilder.append(random.nextInt(10));
            }
            exit=accountRepository.existsByBrojRacuna(stringBuilder.toString());
        }
        Account account=new FxAccount(fxDto.getTipRacuna());
        account.setBrojRacuna("111"+"0001"+ stringBuilder.toString() +fxDto.getTipRacuna().getVal());
        account.setNazivRacuna(fxDto.getNazivRacuna());
        account.setVlasnik(id);
        account.setZaposleni(((Number) jwt.getClaim(appPropertiesId)).longValue());
        account.setDatumIVremeKreiranja(LocalDateTime.now());
        account.setCurrency(currency);
        account.setStatus(Status.ACTIVE);
        account.setCompany(company);
        accountRepository.save(account);
        return "Uspesno kreiran fx account";
    }

    @Override
    public String createCheckingAccount(Jwt jwt, CheckingDto checkingDto) {

        if(checkingDto.getIdVlasnika()==null && (checkingDto.getJmbg()==null||checkingDto.getJmbg().isBlank()))
            throw new IllegalArgumentException("Unesi id ili jmbg");
        if(checkingDto.getFirma()==null && checkingDto.getVrstaRacuna().getAccountOwnershipType() == AccountOwnershipType.BUSINESS || checkingDto.getFirma()!=null && checkingDto.getVrstaRacuna().getAccountOwnershipType() == AccountOwnershipType.PERSONAL)
            throw new IllegalArgumentException("Pogresan tip racuna");

        Currency currency=currencyRepository.findByOznaka(CurrencyCode.RSD).orElse(null);
        if(currency==null )
            throw new IllegalArgumentException("Nije uneta RSD valuta");
        if(currency.getStatus() == Status.INACTIVE)
            throw new IllegalArgumentException("Deaktivirana valuta");
        Company company=null;
        if(checkingDto.getFirma()!=null) {
            SifraDelatnosti sifraDelatnosti = sifraDelatnostiRepository.findByOznaka(checkingDto.getFirma().getSifraDelatnosti()).orElse(null);
            if (sifraDelatnosti == null)
                throw new IllegalArgumentException("Nije uneta sifra delatnosti");
            company = new Company(checkingDto.getFirma().getNaziv(), checkingDto.getFirma().getMaticniBroj(), checkingDto.getFirma().getPoreskiBroj(), sifraDelatnosti, checkingDto.getFirma().getAdresa(), checkingDto.getFirma().getVlasnik());
            company = companyRepository.save(company);
        }
        Long id=null;
        if(checkingDto.getIdVlasnika()!=null)
            id=checkingDto.getIdVlasnika();
        else
        {
            id=clientServiceClient.getUser(checkingDto.getJmbg()).getId();
        }
        StringBuilder stringBuilder=new StringBuilder();
        boolean exit=true;
        while (exit) {
            for (int i = 0; i < 9; i++) {
                stringBuilder.append(random.nextInt(10));
            }
            exit=accountRepository.existsByBrojRacuna(stringBuilder.toString());
        }
        Account account=new CheckingAccount(checkingDto.getVrstaRacuna());
        account.setBrojRacuna("111"+"0001"+ stringBuilder.toString() +checkingDto.getVrstaRacuna().getVal());
        account.setNazivRacuna(checkingDto.getNazivRacuna());
        account.setVlasnik(id);
        account.setZaposleni(((Number) jwt.getClaim(appPropertiesId)).longValue());
        account.setDatumIVremeKreiranja(LocalDateTime.now());
        account.setCurrency(currency);
        account.setStatus(Status.ACTIVE);
        account.setCompany(company);
        accountRepository.save(account);
        return "Uspesno kreiran checking account";
    }

    @Override
    public Page<AccountSearchResponseDto> searchAllAccounts(Jwt jwt, String imeVlasnikaRacuna, String prezimeVlasnikaRacuna, String accountNumber, int page, int size) {
        return null;
    }

    @Override
    public String updateCard(Jwt jwt, Long id, UpdateCardDto updateCardDto) {
        return "";
    }
}
