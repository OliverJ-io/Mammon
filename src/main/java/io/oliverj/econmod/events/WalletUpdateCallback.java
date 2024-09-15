package io.oliverj.econmod.events;

import io.oliverj.econmod.Wallet;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface WalletUpdateCallback {
    Event<WalletUpdateCallback> EVENT = EventFactory.createArrayBacked(WalletUpdateCallback.class,
            (listeners) -> (player, wallet) -> {
        for (WalletUpdateCallback listener : listeners) {
            ActionResult result = listener.interact(player, wallet);

            if (result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
            });

    ActionResult interact(PlayerEntity player, Wallet wallet);
}
