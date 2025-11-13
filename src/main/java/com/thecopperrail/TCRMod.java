package com.thecopperrail;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import java.util.concurrent.atomic.AtomicInteger;

public class TCRMod implements ModInitializer {
	public final static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("thecopperrail","copper_rail");
	public static final Block BLOCK = TCRMod.registerBlock(
		ResourceKey.create(Registries.BLOCK, ID)
	);
	public static final Item ITEM = TCRMod.registerBlockItem(
		ResourceKey.create(Registries.ITEM, ID)
	);

	public static Block registerBlock(ResourceKey<Block> key) {
		return Registry.register(
			BuiltInRegistries.BLOCK, key,
			new CopperRailBlock(
				BlockBehaviour.Properties.ofFullCopy(Blocks.POWERED_RAIL).setId(key)
			)
		);
	}

	public static BlockItem registerBlockItem(ResourceKey<Item> key) {
		return Registry.register(
			BuiltInRegistries.ITEM, key,
			new BlockItem(BLOCK, new Item.Properties().setId(key).useBlockDescriptionPrefix())
		);
	}

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content ->
			content.addAfter(Items.ACTIVATOR_RAIL, ITEM)
		);

		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
    		if (BuiltInLootTables.ABANDONED_MINESHAFT != key || !source.isBuiltin()) {
				return;
			}

			final AtomicInteger i = new AtomicInteger(0);
			tableBuilder.modifyPools(poolBuilder -> {
				if(i.getAndIncrement() < 2){
					return;
				}
				poolBuilder.add(
					LootItem.lootTableItem(ITEM)
					.apply(
						SetItemCountFunction.setCount(UniformGenerator.between(1, 4))
					)
					.setWeight(5)
				);
			});
		});
	}
}