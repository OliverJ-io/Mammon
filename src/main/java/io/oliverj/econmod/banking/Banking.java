package io.oliverj.econmod.banking;

import io.oliverj.econmod.EconMod;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class Banking {
    private Banking() {}

    public static UUID createBank(String name, ServerPlayer owner) {
        BankInfo bankInfo = BankInfo.createBank(name, owner);
        EconMod.banks.put(bankInfo.getId(), bankInfo);
        EconMod.accounts.get(bankInfo.getIssuer()).sign();
        EconMod.accounts.get(bankInfo.getIssuer()).genChecksum();
        return bankInfo.getId();
    }

    public static UUID createBank(ServerPlayer owner) {
        BankInfo bankInfo = BankInfo.createBank(owner);
        EconMod.banks.put(bankInfo.getId(), bankInfo);
        EconMod.accounts.get(bankInfo.getIssuer()).sign();
        EconMod.accounts.get(bankInfo.getIssuer()).genChecksum();
        return bankInfo.getId();
    }

    public static UUID createAccount(ServerPlayer owner, UUID bank, String name) {
        Account account = Account.create(owner, bank, name);
        account.sign();
        account.genChecksum();
        EconMod.accounts.put(account.getAccountId(), account);
        EconMod.users.get(account.getOwner()).addAccount(account.getAccountId());
        return account.getAccountId();
    }

    public static UUID createUser(ServerPlayer player) {
        User user = User.create(player);
        EconMod.users.put(player.getUUID(), user);
        return user.getPlayer();
    }

    public static UUID createNewUser(ServerPlayer player) {
        if (EconMod.users.containsKey(player.getUUID())) return player.getUUID();
        return createUser(player);
    }

    public static UUID _transfer(UUID src, UUID dst, double amount) {
        Transaction transaction = Transaction.transfer(src, dst, amount);
        officiateAndSaveTransaction(transaction);
        return transaction.getTransactionId();
    }

    public static UUID transfer(UUID src, UUID dst, double amount, UUID user) {
        boolean canWithdraw = EconMod.accounts.get(src).canWithdraw(user);
        boolean canDeposit = EconMod.accounts.get(dst).canDeposit(user);

        EconMod.LOGGER.info("{} started transfer (D: {}, W: {})", user, canDeposit, canWithdraw);

        if (!canWithdraw && !canDeposit) return null;
        return _transfer(src, dst, amount);
    }

    public static UUID authorizePayment(UUID src, UUID dst, double amount) {
        boolean hasDeposit = EconMod.accounts.get(dst).canDeposit(EconMod.accounts.get(src).getOwner());
        if (!hasDeposit)
            EconMod.accounts.get(dst).addUserPermission(EconMod.accounts.get(src).getOwner(), AccountPermissions.DEPOSIT);
        UUID tid = transfer(src, dst, amount, EconMod.accounts.get(src).getOwner());
        if (!hasDeposit)
            EconMod.accounts.get(dst).removeUserPermission(EconMod.accounts.get(src).getOwner(), AccountPermissions.DEPOSIT);
        return tid;
    }

    private static void officiateAndSaveTransaction(Transaction transaction) {
        transaction.sign();
        transaction.genChecksum();

        EconMod.accounts.get(transaction.getSourceAccount()).appendTransaction(transaction);
        EconMod.accounts.get(transaction.getSourceAccount()).sign();
        EconMod.accounts.get(transaction.getSourceAccount()).genChecksum();

        EconMod.accounts.get(transaction.getDestinationAccount()).appendTransaction(transaction);
        EconMod.accounts.get(transaction.getDestinationAccount()).sign();
        EconMod.accounts.get(transaction.getDestinationAccount()).genChecksum();

        performTransaction(transaction);
    }

    private static void performTransaction(Transaction transaction) {
        EconMod.accounts.get(transaction.getSourceAccount()).withdraw(transaction.getAmount());
        EconMod.accounts.get(transaction.getDestinationAccount()).deposit(transaction.getAmount());
    }
}
