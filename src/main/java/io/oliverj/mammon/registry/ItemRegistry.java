package io.oliverj.mammon.registry;

import io.oliverj.mammon.Mammon;
import io.oliverj.mammon.items.custom.CardItem;
import io.oliverj.mammon.items.custom.CheckItem;
import io.oliverj.mammon.items.custom.MonetaryNoteItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ItemRegistry {
    public static final Item ONE_MN = registerMonetaryNote("one_bill", 1);
    public static final Item FIVE_MN = registerMonetaryNote("five_bill", 5);
    public static final Item TEN_MN = registerMonetaryNote("ten_bill", 10);
    public static final Item TWENTY_MN = registerMonetaryNote("twenty_bill", 20);
    public static final Item FIFTY_MN = registerMonetaryNote("fifty_bill", 50);
    public static final Item ONE_HUNDRED_MN = registerMonetaryNote("one_hundred_bill", 100);
    public static final Item TWO_HUNDRED_MN = registerMonetaryNote("two_hundred_bill", 200);
    public static final Item FIVE_HUNDRED_MN = registerMonetaryNote("five_hundred_bill", 500);
    public static final Item ONE_THOUSAND_MN = registerMonetaryNote("one_thousand_bill", 1000);

    public static final Item CHECK_ITEM = register("check", CheckItem::new);
    public static final Item CARD_ITEM = register("card", CardItem::new);

    public static void init() {
        Mammon.LOGGER.info("Registering Mammon Items...");
    }

    public static <GenericItem extends Item> GenericItem register(String name, Function<ResourceKey<Item>, GenericItem> itemFactory) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Mammon.id(name));

        GenericItem item = itemFactory.apply(itemKey);

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static MonetaryNoteItem registerMonetaryNote(String name, int value) {
        return register(name, key -> new MonetaryNoteItem(value, key));
    }
}
