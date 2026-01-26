package io.oliverj.econmod.banking;

import io.oliverj.econmod.EconMod;

import java.util.UUID;

public class TransactionValidator {
    public static void checkAccountTransactions(UUID accountId) {
        Account acct = EconMod.accounts.get(accountId);

        EconMod.LOGGER.info("Validating transactions for account {}", accountId);

        boolean transactionFail = false;

        for (Transaction transaction : acct.getTransactions()) {
            if (!transaction.validate()) {
                transactionFail = true;
                EconMod.LOGGER.warn("Transaction {} is invalid", transaction.getTransactionId());
            }
        }

        if (transactionFail)
            EconMod.LOGGER.warn("Account {} owned by {} has failing transactions.", acct.getAccountId(), acct.getOwner());
        else
            EconMod.LOGGER.info("Account {} is valid.", acct.getAccountId());

    }
}
