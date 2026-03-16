package io.oliverj.mammon.client.datagen;

import io.oliverj.mammon.Mammon;
import io.oliverj.mammon.items.custom.MonetaryNoteItem;
import io.oliverj.mammon.registry.ItemRegistry;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class MonetaryNoteModelGenerator extends FabricModelProvider {
    MonetaryNoteModelGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators blockModelGenerators) {}

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        Mammon.LOGGER.info("Generating Item Models...");
        itemModelGenerator.generateFlatItem(ItemRegistry.ONE_MN,          ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.FIVE_MN,         ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.TEN_MN,          ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.TWENTY_MN,       ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.FIFTY_MN,        ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.ONE_HUNDRED_MN,  ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.TWO_HUNDRED_MN,  ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.FIVE_HUNDRED_MN, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.ONE_THOUSAND_MN, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.CHECK_ITEM,      ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ItemRegistry.CARD_ITEM,       ModelTemplates.FLAT_ITEM);
    }
}

class EnglishTranslationProvider extends FabricLanguageProvider {
    EnglishTranslationProvider(FabricDataOutput dataGenerator, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataGenerator, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, @NonNull TranslationBuilder translationBuilder) {
        Mammon.LOGGER.info("Generating en_us Translations...");
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
        translationBuilder.add("item.econmod.card", "%s - Card");
    }
}

public class MammonDataGeneration implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        Mammon.LOGGER.info("Starting Mammon Data Generation");
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(MonetaryNoteModelGenerator::new);
        pack.addProvider(EnglishTranslationProvider::new);
    }
}
