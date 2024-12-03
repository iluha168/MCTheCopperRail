package com.thecopperrail.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.DefaultMinecartController;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.thecopperrail.CopperRailBlock;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DefaultMinecartController.class)
public class DefaultMinecartControllerMixin {
	@Redirect(
		method = "moveOnRail",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
			ordinal = 0
		),
		require = 1
	)
	private boolean injectedPoweredRailCheck(BlockState state, Block POWERED_RAIL) {
		return state.isOf(POWERED_RAIL) || state.isOf(CopperRailBlock.BLOCK);
	}

	@Inject(
		method = "moveOnRail",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/vehicle/DefaultMinecartController;getVelocity()Lnet/minecraft/util/math/Vec3d;",
			shift = At.Shift.BEFORE,
			ordinal = 10
		),
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true,
		require = 1
	)
	private void setNewVelocity(ServerWorld world, CallbackInfo ci, BlockPos blockPos, BlockState blockState, double v0, double v1, double v2, Vec3d v3, boolean v4, boolean v5, double v6, Vec3d v7, RailShape railShape) {
		if (!blockState.isOf(CopperRailBlock.BLOCK)) {
			return;
		}
		ci.cancel();

		DefaultMinecartController This = (DefaultMinecartController)(Object)this;
		Vec3d pushForce = CopperRailBlock.getPushForce(blockState);
		Vec3d velocity = This.getVelocity();

		double speed = velocity.horizontalLength();
		if (speed > 0.01) {
			double correlation = pushForce.dotProduct(velocity.normalize());
			double factor = 0.06/speed;
			This.setVelocity(
				(correlation > 0.5)?
					// Vanilla acceleration
					velocity.add(velocity.x*factor, 0.0, velocity.z*factor)
					// Approximate deceleration
				  : velocity.subtract(velocity.x*factor, 0.0, velocity.z*factor)
			);
		} else {
			// Minecart is stopped, TCR starts it without the need of a block
			This.setVelocity(pushForce.multiply(0.02));
		}
	}
}