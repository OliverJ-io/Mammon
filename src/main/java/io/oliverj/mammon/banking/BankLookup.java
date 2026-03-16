package io.oliverj.mammon.banking;

import io.oliverj.mammon.Mammon;

import java.util.UUID;

public class BankLookup {
    public static BankInfo getBankFromAccount(Account acct) {
        return Mammon.banks.get(acct.getBank());
    }

    public static BankInfo getBankFromAccount(UUID accountId) {
        return Mammon.banks.get(Mammon.accounts.get(accountId).getBank());
    }
}
