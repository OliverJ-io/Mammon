package io.oliverj.econmod.events;

import io.oliverj.econmod.Wallet;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public interface WalletUpdateCallback {
    Event<WalletUpdateCallback> EVENT = EventFactory.createArrayBacked(WalletUpdateCallback.class,
            (listeners) -> (player, wallet) -> {
        for (WalletUpdateCallback listener : listeners) {
            InteractionResult result = listener.interact(player, wallet);

            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        return InteractionResult.PASS;
            });

    InteractionResult interact(Player player, Wallet wallet);
}
