package io.oliverj.mammon.banking;

import io.oliverj.mammon.Mammon;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class Banking {
    private Banking() {}

    public static UUID createBank(String name, ServerPlayer owner) {
        BankInfo bankInfo = BankInfo.createBank(name, owner);
        Mammon.banks.put(bankInfo.getId(), bankInfo);
        Mammon.accounts.get(bankInfo.getIssuer()).sign();
        Mammon.accounts.get(bankInfo.getIssuer()).genChecksum();
        return bankInfo.getId();
    }

    public static UUID createBank(ServerPlayer owner) {
        BankInfo bankInfo = BankInfo.createBank(owner);
        Mammon.banks.put(bankInfo.getId(), bankInfo);
        Mammon.accounts.get(bankInfo.getIssuer()).sign();
        Mammon.accounts.get(bankInfo.getIssuer()).genChecksum();
        return bankInfo.getId();
    }

    public static UUID createAccount(ServerPlayer owner, UUID bank, String name) {
        Account account = Account.create(owner, bank, name);
        account.sign();
        account.genChecksum();
        Mammon.accounts.put(account.getAccountId(), account);
        Mammon.users.get(account.getOwner()).addAccount(account.getAccountId());
        return account.getAccountId();
    }

    public static UUID createUser(ServerPlayer player) {
        User user = User.create(player);
        Mammon.users.put(player.getUUID(), user);
        return user.getPlayer();
    }

    public static UUID createNewUser(ServerPlayer player) {
        if (Mammon.users.containsKey(player.getUUID())) return player.getUUID();
        return createUser(player);
    }

    public static UUID _transfer(UUID src, UUID dst, double amount) {
        if(!Mammon.accounts.get(src).validate() || !Mammon.accounts.get(dst).validate()) return null;

        Transaction transaction = Transaction.transfer(src, dst, amount);
        officiateAndSaveTransaction(transaction);
        return transaction.getTransactionId();
    }

    public static UUID transfer(UUID src, UUID dst, double amount, UUID user) {
        boolean canWithdraw = Mammon.accounts.get(src).canWithdraw(user);
        boolean canDeposit = Mammon.accounts.get(dst).canDeposit(user);

        Mammon.LOGGER.info("{} started transfer (D: {}, W: {})", user, canDeposit, canWithdraw);

        if (!canWithdraw && !canDeposit) return null;
        return _transfer(src, dst, amount);
    }

    public static UUID authorizePayment(UUID src, UUID dst, double amount) {
        boolean hasDeposit = Mammon.accounts.get(dst).canDeposit(Mammon.accounts.get(src).getOwner());
        if (!hasDeposit) {
            Mammon.LOGGER.info("{} is not permitted to deposit. Granting privileges", Mammon.accounts.get(src).getOwner());
            Mammon.accounts.get(dst).addUserPermission(Mammon.accounts.get(src).getOwner(), AccountPermissions.DEPOSIT);
            Mammon.accounts.get(dst).sign();
            Mammon.accounts.get(dst).genChecksum();
        }
        Mammon.LOGGER.info("Transferring {} from {} to {}", amount, src, dst);
        UUID tid = transfer(src, dst, amount, Mammon.accounts.get(src).getOwner());
        if (!hasDeposit) {
            Mammon.accounts.get(dst).removeUserPermission(Mammon.accounts.get(src).getOwner(), AccountPermissions.DEPOSIT);
            Mammon.LOGGER.info("Revoking deposit privileges for {} on {}", Mammon.accounts.get(src).getOwner(), dst);
            Mammon.accounts.get(dst).sign();
            Mammon.accounts.get(dst).genChecksum();
        }
        return tid;
    }

    private static void officiateAndSaveTransaction(Transaction transaction) {
        transaction.sign();
        transaction.genChecksum();

        Mammon.accounts.get(transaction.getSourceAccount()).appendTransaction(transaction);
        Mammon.accounts.get(transaction.getSourceAccount()).sign();
        Mammon.accounts.get(transaction.getSourceAccount()).genChecksum();

        Mammon.accounts.get(transaction.getDestinationAccount()).appendTransaction(transaction);
        Mammon.accounts.get(transaction.getDestinationAccount()).sign();
        Mammon.accounts.get(transaction.getDestinationAccount()).genChecksum();

        performTransaction(transaction);
    }

    private static void performTransaction(Transaction transaction) {
        Mammon.accounts.get(transaction.getSourceAccount()).withdraw(transaction.getAmount());
        Mammon.accounts.get(transaction.getDestinationAccount()).deposit(transaction.getAmount());
    }
}
