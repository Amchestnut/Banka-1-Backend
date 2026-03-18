package com.banka1.account_service.domain;

import com.banka1.account_service.domain.enums.AccountConcrete;
import com.banka1.account_service.domain.enums.AccountOwnershipType;
import com.banka1.account_service.domain.enums.Status;
import com.banka1.account_service.domain.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("CHECKING")

public class CheckingAccount extends Account{
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountConcrete accountConcrete;
    private BigDecimal odrzavanjeRacuna= BigDecimal.ZERO;


    public CheckingAccount(AccountConcrete accountConcrete) {
        this.accountConcrete = accountConcrete;
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (accountConcrete.getAccountOwnershipType() == AccountOwnershipType.BUSINESS && this.getCompany() == null) {
            throw new IllegalStateException("Company is required for BUSINESS account");
        }

        if (accountConcrete.getAccountOwnershipType() == AccountOwnershipType.PERSONAL && this.getCompany() != null) {
            throw new IllegalStateException("Company must be null for PERSONAL account");
        }
    }

    @Override
    public void setCurrency(Currency currency) {
        if(currency.getOznaka()!=CurrencyCode.RSD)
            throw new IllegalArgumentException("Mora RSD");
        super.setCurrency(currency);
    }
}
