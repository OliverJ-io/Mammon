package io.oliverj.econmod.banking;

import io.oliverj.econmod.EconMod;

import java.util.UUID;

public class BankLookup {
    public static BankInfo getBankFromAccount(Account acct) {
        return EconMod.banks.get(acct.getBank());
    }

    public static BankInfo getBankFromAccount(UUID accountId) {
        return EconMod.banks.get(EconMod.accounts.get(accountId).getBank());
    }
}
