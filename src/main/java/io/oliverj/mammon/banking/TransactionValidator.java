package io.oliverj.mammon.banking;

import io.oliverj.mammon.Mammon;

import java.util.UUID;

public class TransactionValidator {
    public static void checkAccountTransactions(UUID accountId) {
        Account acct = Mammon.accounts.get(accountId);

        Mammon.LOGGER.info("Validating transactions for account {}", accountId);

        boolean transactionFail = false;

        for (Transaction transaction : acct.getTransactions()) {
            if (!transaction.validate()) {
                transactionFail = true;
                Mammon.LOGGER.warn("Transaction {} is invalid", transaction.getTransactionId());
            }
        }

        if (transactionFail)
            Mammon.LOGGER.warn("Account {} owned by {} has failing transactions.", acct.getAccountId(), acct.getOwner());
        else
            Mammon.LOGGER.info("Account {} is valid.", acct.getAccountId());

    }
}
