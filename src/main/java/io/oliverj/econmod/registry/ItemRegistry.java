package io.oliverj.econmod.registry;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.items.custom.CardItem;
import io.oliverj.econmod.items.custom.CheckItem;
import io.oliverj.econmod.items.custom.MonetaryNoteItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemRegistry {
    public static final Item ONE_MN = new MonetaryNoteItem(1);

    public static final Item FIVE_MN = new MonetaryNoteItem(5);

    public static final Item TEN_MN = new MonetaryNoteItem(10);

    public static final Item TWENTY_MN = new MonetaryNoteItem(20);

    public static final Item FIFTY_MN = new MonetaryNoteItem(50);

    public static final Item ONE_HUNDRED_MN = new MonetaryNoteItem(100);

    public static final Item TWO_HUNDRED_MN = new MonetaryNoteItem(200);

    public static final Item FIVE_HUNDRED_MN = new MonetaryNoteItem(500);

    public static final Item ONE_THOUSAND_MN = new MonetaryNoteItem(1000);

    public static final Item CHECK_ITEM = new CheckItem();

    public static final Item CARD_ITEM = new CardItem();

    public static final BlockItem CARD_READER = new BlockItem(BlockRegistry.CARD_READER, new Item.Settings());

    public static void init() {
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "one_bill"), ONE_MN);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "five_bill"), FIVE_MN);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "ten_bill"), TEN_MN);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "twenty_bill"), TWENTY_MN);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "fifty_bill"), FIFTY_MN);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "one_hundred_bill"), ONE_HUNDRED_MN);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "two_hundred_bill"), TWO_HUNDRED_MN);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "five_hundred_bill"), FIVE_HUNDRED_MN);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "one_thousand_bill"), ONE_THOUSAND_MN);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "check"), CHECK_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "card"), CARD_ITEM);

        Registry.register(Registries.ITEM, Identifier.of(EconMod.MOD_ID, "card_reader"), CARD_READER);
    }
}
