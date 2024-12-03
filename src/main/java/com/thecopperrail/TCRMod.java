package com.thecopperrail;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

import java.util.concurrent.atomic.AtomicInteger;

public class TCRMod implements ModInitializer {
	public final static Identifier ID = Identifier.of("thecopperrail","copper_rail");

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content ->
			content.addAfter(Items.ACTIVATOR_RAIL, CopperRailBlock.ITEM)
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
					ItemEntry.builder(CopperRailBlock.ITEM)
					.apply(
						SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))
					)
					.weight(5)
				);
			});
		});
	}
}