package io.oliverj.econmod.utils;

import io.oliverj.econmod.EconMod;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class EconText extends TranslatableContents {

    public EconText() {
        super(EconMod.MOD_ID, EconMod.MOD_NAME, NO_ARGS);
    }

    public EconText(String string) {
        super(EconMod.MOD_ID + "." + string, null, NO_ARGS);
    }

    public EconText(String string, Object... args) {
        super(EconMod.MOD_ID + "." + string, null, args);
    }

    public static MutableComponent of() {
        return MutableComponent.create(new EconText());
    }

    public static MutableComponent of(String string) {
        return MutableComponent.create(new EconText(string));
    }

    public static MutableComponent of(String string, Object... args) {
        return MutableComponent.create(new EconText(string, args));
    }
}
