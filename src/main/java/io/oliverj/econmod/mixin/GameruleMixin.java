package io.oliverj.econmod.mixin;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.oliverj.econmod.Gamerules;
import io.oliverj.econmod.Payloads;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRuleCommand.class)
public abstract class GameruleMixin {
    @Inject(at = @At("HEAD"), method = "executeSet")
    private static <T extends GameRules.Rule<T>> void sendGamerule(CommandContext<ServerCommandSource> context, GameRules.Key<T> key, CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = context.getSource().getServer();

        Gamerules.Type type;

        String gameruleName = key.getName();

        if (gameruleName.equals(Gamerules.DEBT_FLOOR.getName())) type = Gamerules.Type.DOUBLE;
        else return;

        switch (type) {
            case DOUBLE -> {
                double value = DoubleArgumentType.getDouble(context, "value");

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    server.execute(() -> ServerPlayNetworking.send(player, new Payloads.GamerulePayloads.Double(gameruleName, value)));
                }
            }
        }
    }
}
