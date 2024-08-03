package com.thecopperrail;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.registry.Registry;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public class TCRMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Identifier CopperRailID = Identifier.of("thecopperrail","copper_rail");
		Registry.register(Registries.BLOCK, CopperRailID, CopperRailBlock.BLOCK     );
		Registry.register(Registries.ITEM , CopperRailID, CopperRailBlock.BLOCK_ITEM);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
			content.addAfter(Items.ACTIVATOR_RAIL, CopperRailBlock.BLOCK_ITEM);
		});
		LootTableEvents.MODIFY.register((id, tableBuilder, source) -> {
    		if (source.isBuiltin() && LootTables.ABANDONED_MINESHAFT_CHEST.equals(id)) {
				final int[] i = new int[]{0};
				tableBuilder.modifyPools(poolBuilder -> {
					if(i[0]++ == 2){
						poolBuilder.with(
							ItemEntry.builder(CopperRailBlock.BLOCK_ITEM)
							.apply(
								SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))
							)
							.weight(5)
						);
					}
				});
    		}
		});
	}
}