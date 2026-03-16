package io.oliverj.mammon.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.oliverj.mammon.Mammon;
import io.oliverj.mammon.banking.BankInfo;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BankSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        Collection<String> bankNames = Mammon.banks.values().stream().map(BankInfo::getName).toList();

        for (String name : bankNames) {
            builder.suggest(name);
        }

        return builder.buildFuture();
    }
}
