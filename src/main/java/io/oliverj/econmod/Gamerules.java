package io.oliverj.econmod;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class Gamerules {
    public static final GameRules.Key<GameRules.IntRule> DEBT_FLOOR = GameRuleRegistry.register("debtFloor", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(0));

    public enum Type {
        INTEGER,
        DOUBLE;
    }

    public static void RegisterGamerules() {}
}
