package io.oliverj.econmod;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;

public class GameRules {
    public static final GameRule<Integer> DEBT_FLOOR = GameRuleBuilder.forInteger(0)
            .buildAndRegister(EconMod.id("debt_floor"));

    public enum Type {
        INTEGER,
        DOUBLE
    }

    public static void registerGameRules() {}
}
