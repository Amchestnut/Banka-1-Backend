package com.banka1.account_service.dto.response;

import com.banka1.account_service.domain.Account;
import com.banka1.account_service.domain.CheckingAccount;
import com.banka1.account_service.domain.FxAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountResponseDto {
    private String nazivRacuna;
    private String brojRacuna;
    private BigDecimal raspolozivoStanje;
    private String currency;
    private String accountCategory;
    private String accountType;
    private String subtype;

    public AccountResponseDto(Account account) {
        this.nazivRacuna = account.getNazivRacuna();
        this.brojRacuna = account.getBrojRacuna();
        this.raspolozivoStanje = account.getRaspolozivoStanje();
        this.currency = account.getCurrency() != null ? account.getCurrency().getOznaka().name() : null;
        if (account instanceof CheckingAccount ca) {
            this.accountCategory = "CHECKING";
            this.accountType = ca.getAccountConcrete().getAccountOwnershipType().name();
            this.subtype = ca.getAccountConcrete().name();
        } else if (account instanceof FxAccount fa) {
            this.accountCategory = "FOREIGN_CURRENCY";
            this.accountType = fa.getAccountOwnershipType().name();
            this.subtype = null;
        }
    }
}
