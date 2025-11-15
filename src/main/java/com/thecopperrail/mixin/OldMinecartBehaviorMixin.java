package com.thecopperrail.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.thecopperrail.TCRMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.OldMinecartBehavior;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.thecopperrail.CopperRailBlock;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OldMinecartBehavior.class)
public class OldMinecartBehaviorMixin {
	@Redirect(
		method = "moveAlongTrack",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
			ordinal = 0
		),
		require = 1
	)
	private boolean redirectedPoweredRailCheck(BlockState state, Block POWERED_RAIL) {
		return state.is(POWERED_RAIL) || state.is(TCRMod.BLOCK);
	}

	@Inject(
		method = "moveAlongTrack",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/OldMinecartBehavior;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;",
			shift = At.Shift.BEFORE,
			ordinal = 10
		),
		cancellable = true,
		require = 1
	)
	private void setNewVelocity(ServerLevel world, CallbackInfo ci, @Local BlockState blockState) {
		if (!blockState.is(TCRMod.BLOCK)) {
			return;
		}
		ci.cancel();

		OldMinecartBehavior This = (OldMinecartBehavior)(Object)this;
		Vec3 pushForce = CopperRailBlock.getPushForce(blockState);
		Vec3 velocity = This.getDeltaMovement();

		double speed = velocity.horizontalDistance();
		if (speed > 0.01) {
			double correlation = pushForce.dot(velocity.normalize());
			double factor = 0.06/speed;
			This.setDeltaMovement(
				(correlation > 0.5)?
					// Vanilla acceleration
					velocity.add(velocity.x*factor, 0.0, velocity.z*factor)
					// Approximate deceleration
				  : velocity.subtract(velocity.x*factor, 0.0, velocity.z*factor)
			);
		} else {
			// Minecart is stopped, TCR starts it without the need of a block
			This.setDeltaMovement(pushForce.scale(0.02));
		}
	}
}