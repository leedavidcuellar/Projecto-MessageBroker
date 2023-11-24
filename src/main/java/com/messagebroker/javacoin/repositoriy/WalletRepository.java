package com.messagebroker.javacoin.repositoriy;

import com.messagebroker.javacoin.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Wallet findWalletByDniWallet(String dni);
}
