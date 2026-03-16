package io.oliverj.mammon.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.oliverj.mammon.Mammon;
import io.oliverj.mammon.banking.Account;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class AccountSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        Collection<String> accountNames = Mammon.accounts.values().stream().filter(acc ->
            acc.isOwner(commandContext.getSource().getEntity().getUUID())
        ).map(Account::getName).toList();

        for (String name : accountNames) {
            suggestionsBuilder.suggest(name);
        }

        return suggestionsBuilder.buildFuture();
    }
}
