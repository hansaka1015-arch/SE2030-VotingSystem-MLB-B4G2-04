package org.example.voting.service;

public interface CreditLedger {
    int checkCreditBalance(String userId);
    void debitCredit(String userId, int amount);
}