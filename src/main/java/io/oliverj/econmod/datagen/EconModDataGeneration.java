package io.oliverj.econmod.datagen;

import io.oliverj.econmod.block.custom.CardReaderBlock;
import io.oliverj.econmod.registry.ItemRegistry;
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

class BlockModelGenerator extends FabricModelProvider {
    BlockModelGenerator(FabricDataOutput generator) { super(generator); }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(BlockRegistry.CARD_READER);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {}
}

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
        itemModelGenerator.register(ItemRegistry.ONE_MN,          Models.GENERATED);
        itemModelGenerator.register(ItemRegistry.FIVE_MN,         Models.GENERATED);
        itemModelGenerator.register(ItemRegistry.TEN_MN,          Models.GENERATED);
        itemModelGenerator.register(ItemRegistry.TWENTY_MN,       Models.GENERATED);
        itemModelGenerator.register(ItemRegistry.FIFTY_MN,        Models.GENERATED);
        itemModelGenerator.register(ItemRegistry.ONE_HUNDRED_MN,  Models.GENERATED);
        itemModelGenerator.register(ItemRegistry.TWO_HUNDRED_MN,  Models.GENERATED);
        itemModelGenerator.register(ItemRegistry.FIVE_HUNDRED_MN, Models.GENERATED);
        itemModelGenerator.register(ItemRegistry.ONE_THOUSAND_MN, Models.GENERATED);
        itemModelGenerator.register(ItemRegistry.CHECK_ITEM,      Models.GENERATED);
    }
}

class EnglishTranslationProvider extends FabricLanguageProvider {
    EnglishTranslationProvider(FabricDataOutput dataGenerator, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataGenerator, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        List<Item> items = new ArrayList<>();
        items.add(ItemRegistry.ONE_MN);
        items.add(ItemRegistry.FIVE_MN);
        items.add(ItemRegistry.TEN_MN);
        items.add(ItemRegistry.TWENTY_MN);
        items.add(ItemRegistry.FIFTY_MN);
        items.add(ItemRegistry.ONE_HUNDRED_MN);
        items.add(ItemRegistry.TWO_HUNDRED_MN);
        items.add(ItemRegistry.FIVE_HUNDRED_MN);
        items.add(ItemRegistry.ONE_THOUSAND_MN);

        for (Item item : items) {
            MonetaryNoteItem note = (MonetaryNoteItem) item;
            translationBuilder.add(item, note.getValue() + " ¤");
        }

        translationBuilder.add("wallet.balance.tooltip", "%d ¤");
        translationBuilder.add(ItemRegistry.CHECK_ITEM, "Check");
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
        pack.addProvider(BlockModelGenerator::new);
    }
}
