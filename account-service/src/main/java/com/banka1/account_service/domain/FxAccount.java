package com.banka1.account_service.domain;

import com.banka1.account_service.domain.enums.CurrencyCode;
import com.banka1.account_service.domain.enums.Status;
import com.banka1.account_service.domain.enums.AccountOwnershipType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
@DiscriminatorValue("FX")
@AllArgsConstructor
//todo fk firma instanca
public class FxAccount extends Account {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountOwnershipType accountOwnershipType;


    @PrePersist
    @PreUpdate
    private void validate() {
        if (accountOwnershipType == AccountOwnershipType.BUSINESS && this.getCompany() == null) {
            throw new IllegalStateException("Company is required for BUSINESS account");
        }

        if (accountOwnershipType == AccountOwnershipType.PERSONAL && this.getCompany() != null) {
            throw new IllegalStateException("Company must be null for PERSONAL account");
        }
    }

    public void setCurrency(Currency currency) {
        if(currency.getOznaka()== CurrencyCode.RSD)
            throw new IllegalArgumentException("Ne moze RSD");
        super.setCurrency(currency);
    }
}
