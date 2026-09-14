package org.example.voting.repository;

import org.example.voting.model.CreditWallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditWalletRepository extends JpaRepository<CreditWallet, Integer> {
}