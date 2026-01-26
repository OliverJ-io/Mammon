package io.oliverj.econmod.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.banking.BankInfo;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BankSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        Collection<String> bankNames = EconMod.banks.values().stream().map(BankInfo::getName).toList();

        for (String name : bankNames) {
            builder.suggest(name);
        }

        return builder.buildFuture();
    }
}
