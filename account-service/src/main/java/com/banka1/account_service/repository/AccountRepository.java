package com.banka1.account_service.repository;

import com.banka1.account_service.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    boolean existsByBrojRacuna(String brojRacuna);
}
