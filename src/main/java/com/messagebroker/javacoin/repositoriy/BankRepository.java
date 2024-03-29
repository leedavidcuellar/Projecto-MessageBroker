package com.messagebroker.javacoin.repositoriy;

import com.messagebroker.javacoin.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface BankRepository extends JpaRepository<Bank,Long> {
    Bank findBankByDniAccount(String dni);
}
