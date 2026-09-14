package org.example.voting.repository;

import org.example.voting.model.CreditWallet;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CreditWalletRepository extends Repository<CreditWallet, Integer> {
    <S extends CreditWallet> S save(S entity);
    Optional<CreditWallet> findById(Integer id);
}