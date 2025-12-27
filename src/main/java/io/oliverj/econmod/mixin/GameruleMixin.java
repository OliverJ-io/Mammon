package io.oliverj.econmod.mixin;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.oliverj.econmod.GameRules;
import io.oliverj.econmod.Payloads;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRuleCommand.class)
public abstract class GameruleMixin {
    @Inject(at = @At("HEAD"), method = "setRule")
    private static <T extends GameRule<T>> void sendGamerule(CommandContext<CommandSourceStack> context, GameRule<T> gameRule, CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = context.getSource().getServer();

        GameRules.Type type;

        String gameruleName = gameRule.id();

        if (gameruleName.equals(GameRules.DEBT_FLOOR.id())) type = GameRules.Type.DOUBLE;
        else return;

        if (type == GameRules.Type.DOUBLE) {
            double value = DoubleArgumentType.getDouble(context, "value");

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                server.execute(() -> ServerPlayNetworking.send(player, new Payloads.GamerulePayloads.Double(gameruleName, value)));
            }
        }
    }
}
