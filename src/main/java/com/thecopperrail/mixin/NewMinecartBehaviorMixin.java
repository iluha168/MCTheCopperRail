package com.thecopperrail.mixin;

import com.thecopperrail.CopperRailBlock;
import com.thecopperrail.TCRMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin {
	@Redirect(
		method = "calculateHaltTrackSpeed",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
			ordinal = 0
		),
		require = 1
	)
	private boolean redirectedPoweredRailCheck(BlockState state, Block block) {
		return state.is(block) || state.is(TCRMod.BLOCK);
	}

	@Inject(
		method = "calculateBoostTrackSpeed",
		at = @At(value = "HEAD"),
		cancellable = true,
		require = 1
	)
	private void setNewVelocity(Vec3 velocity, BlockPos railPos, BlockState railState, CallbackInfoReturnable<Vec3> cir) {
		if (railState.is(TCRMod.BLOCK) && railState.getValue(PoweredRailBlock.POWERED)) {
			cir.cancel();
			Vec3 pushForce = CopperRailBlock.getPushForce(railState);
			double vLen = velocity.length();
			if (vLen > 0.01) {
				Vec3 normalV = velocity.normalize();
				// Both pushForce and normalV are normal,
				// therefore their dot product is cos(angle between them)
				double correlation = pushForce.dot(normalV);
				cir.setReturnValue(
					correlation > 0.5?
					// Vanilla acceleration formula
					normalV.scale(vLen + 0.06)
					// A rough inverse of vanilla acceleration
					: velocity.scale(vLen/(vLen+0.06))
				);
			} else {
				cir.setReturnValue(pushForce.scale(vLen + 0.2));
			}
		}
	}
}