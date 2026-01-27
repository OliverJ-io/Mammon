package io.oliverj.econmod.blocks.custom;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.Payloads;
import io.oliverj.econmod.screen.ATMMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ATMBlock extends Block {
    public ATMBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.PASS;
        Map<UUID, Component> acctOwnerMap = new HashMap<>();
        EconMod.accounts.forEach((uuid, account) -> {
            if (EconMod.MC_SERVER.getPlayerList().getPlayer(account.getOwner()) != null)
                acctOwnerMap.put(uuid, Objects.requireNonNull(EconMod.MC_SERVER.getPlayerList().getPlayer(account.getOwner())).getName());
        });

        ServerPlayNetworking.send((ServerPlayer) player, new Payloads.ATMFriendlyMappingsPayload(acctOwnerMap));
        player.openMenu(state.getMenuProvider(level, pos));

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((i, inventory, player) -> new ATMMenu(i, inventory), Component.literal("About"));
    }
}
