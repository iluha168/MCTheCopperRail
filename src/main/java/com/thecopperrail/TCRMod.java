package com.thecopperrail;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

import java.util.concurrent.atomic.AtomicInteger;

public class TCRMod implements ModInitializer {
	public final static Identifier ID = Identifier.of("thecopperrail","copper_rail");
	public static final Block BLOCK = TCRMod.registerBlock(
		RegistryKey.of(RegistryKeys.BLOCK, ID)
	);
	public static final Item ITEM = TCRMod.registerBlockItem(
		RegistryKey.of(RegistryKeys.ITEM, ID)
	);

	public static Block registerBlock(RegistryKey<Block> key) {
		return Registry.register(
			Registries.BLOCK, key,
			new CopperRailBlock(
				AbstractBlock.Settings.copy(Blocks.POWERED_RAIL).registryKey(key)
			)
		);
	}

	public static BlockItem registerBlockItem(RegistryKey<Item> key) {
		return Registry.register(
			Registries.ITEM, key,
			new BlockItem(BLOCK, new Item.Settings().registryKey(key).useBlockPrefixedTranslationKey())
		);
	}

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content ->
			content.addAfter(Items.ACTIVATOR_RAIL, ITEM)
		);

		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
    		if (LootTables.ABANDONED_MINESHAFT_CHEST != key || !source.isBuiltin()) {
				return;
			}

			final AtomicInteger i = new AtomicInteger(0);
			tableBuilder.modifyPools(poolBuilder -> {
				if(i.getAndIncrement() < 2){
					return;
				}
				poolBuilder.with(
					ItemEntry.builder(ITEM)
					.apply(
						SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))
					)
					.weight(5)
				);
			});
		});
	}
}