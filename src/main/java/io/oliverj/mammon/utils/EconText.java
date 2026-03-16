package io.oliverj.mammon.utils;

import io.oliverj.mammon.Mammon;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class EconText extends TranslatableContents {

    public EconText() {
        super(Mammon.MOD_ID, Mammon.MOD_NAME, NO_ARGS);
    }

    public EconText(String string) {
        super(Mammon.MOD_ID + "." + string, null, NO_ARGS);
    }

    public EconText(String string, Object... args) {
        super(Mammon.MOD_ID + "." + string, null, args);
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
