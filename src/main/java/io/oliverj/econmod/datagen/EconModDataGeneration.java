package io.oliverj.econmod.datagen;

import io.oliverj.econmod.items.ItemRegister;
import io.oliverj.econmod.items.custom.MonetaryNoteItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class MonetaryNoteModelGenerator extends FabricModelProvider {
    MonetaryNoteModelGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ItemRegister.ONE_MN,          Models.GENERATED);
        itemModelGenerator.register(ItemRegister.FIVE_MN,         Models.GENERATED);
        itemModelGenerator.register(ItemRegister.TEN_MN,          Models.GENERATED);
        itemModelGenerator.register(ItemRegister.TWENTY_MN,       Models.GENERATED);
        itemModelGenerator.register(ItemRegister.FIFTY_MN,        Models.GENERATED);
        itemModelGenerator.register(ItemRegister.ONE_HUNDRED_MN,  Models.GENERATED);
        itemModelGenerator.register(ItemRegister.TWO_HUNDRED_MN,  Models.GENERATED);
        itemModelGenerator.register(ItemRegister.FIVE_HUNDRED_MN, Models.GENERATED);
        itemModelGenerator.register(ItemRegister.ONE_THOUSAND_MN, Models.GENERATED);
        itemModelGenerator.register(ItemRegister.CHECK_ITEM,      Models.GENERATED);
    }
}

class EnglishTranslationProvider extends FabricLanguageProvider {
    EnglishTranslationProvider(FabricDataOutput dataGenerator, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataGenerator, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        List<Item> items = new ArrayList<>();
        items.add(ItemRegister.ONE_MN);
        items.add(ItemRegister.FIVE_MN);
        items.add(ItemRegister.TEN_MN);
        items.add(ItemRegister.TWENTY_MN);
        items.add(ItemRegister.FIFTY_MN);
        items.add(ItemRegister.ONE_HUNDRED_MN);
        items.add(ItemRegister.TWO_HUNDRED_MN);
        items.add(ItemRegister.FIVE_HUNDRED_MN);
        items.add(ItemRegister.ONE_THOUSAND_MN);

        for (Item item : items) {
            MonetaryNoteItem note = (MonetaryNoteItem) item;
            translationBuilder.add(item, note.getValue() + " ¤");
        }

        translationBuilder.add("wallet.balance.tooltip", "%d ¤");
        translationBuilder.add(ItemRegister.CHECK_ITEM, "Check");
        translationBuilder.add("container.econmod.check", "Blank Check");
        translationBuilder.add("container.econmod.check.sign", "Sign");
    }
}

public class EconModDataGeneration implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(MonetaryNoteModelGenerator::new);
        pack.addProvider(EnglishTranslationProvider::new);
    }
}
