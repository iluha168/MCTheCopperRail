package com.thecopperrail.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thecopperrail.CopperRailBlock;

@Debug(export = true)
@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartMixin {
	@Redirect(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"), require = 1)
	private boolean injectedPoweredRailCheck(BlockState state, Block POWERED_RAIL) {
		return state.isOf(POWERED_RAIL) || state.isOf(CopperRailBlock.BLOCK);
	}

	@Inject(method = "moveOnRail", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
		shift = At.Shift.AFTER,
		ordinal = 9
	), cancellable = true, require = 1)
	private void injectedCopperRailCallback(BlockPos pos, BlockState state, CallbackInfo ci) {
		if(state.isOf(CopperRailBlock.BLOCK)){
			((CopperRailBlock)state.getBlock()).affectMinecart((AbstractMinecartEntity)(Object)this, state);
  			ci.cancel();
		}
	}
}