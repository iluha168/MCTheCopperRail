package com.thecopperrail.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.thecopperrail.CopperRailBlock;
import com.thecopperrail.TCRMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin {
	@Inject(
		method = "getRedstoneDirection",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
		),
		require = 1,
		cancellable = true,
		order = 100000
	)
	void getPushDirection(BlockPos blockPos, CallbackInfoReturnable<Vec3> cir, @Local(ordinal = 0) BlockState railState) {
		if (railState.is(TCRMod.BLOCK) && railState.getValue(PoweredRailBlock.POWERED)) {
			cir.setReturnValue(CopperRailBlock.getPushForce(railState));
		}
	}
}