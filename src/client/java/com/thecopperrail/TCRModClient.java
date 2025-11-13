package com.thecopperrail;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class TCRModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.putBlock(TCRMod.BLOCK, ChunkSectionLayer.CUTOUT);
	}	
}