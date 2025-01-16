package com.thecopperrail.mixin;

import com.thecopperrail.CopperRailBlock;
import com.thecopperrail.TCRMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperimentalMinecartController.class)
public abstract class ExperimentalMinecartControllerMixin {
	@Redirect(
		method = "decelerateFromPoweredRail",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
			ordinal = 0
		),
		require = 1
	)
	private boolean injectedPoweredRailCheck(BlockState state, Block block) {
		return state.isOf(block) || state.isOf(TCRMod.BLOCK);
	}

	@Inject(
		method = "accelerateFromPoweredRail",
		at = @At(value = "HEAD"),
		cancellable = true,
		require = 1
	)
	private void setNewVelocity(Vec3d velocity, BlockPos railPos, BlockState railState, CallbackInfoReturnable<Vec3d> cir) {
		if (railState.isOf(TCRMod.BLOCK) && railState.get(PoweredRailBlock.POWERED)) {
			cir.cancel();
			Vec3d pushForce = CopperRailBlock.getPushForce(railState);
			double vLen = velocity.length();
			if (vLen > 0.01) {
				Vec3d normalV = velocity.normalize();
				// Both pushForce and normalV are normal,
				// therefore their dot product is cos(angle between them)
				double correlation = pushForce.dotProduct(normalV);
				cir.setReturnValue(
					correlation > 0.5?
					// Vanilla acceleration formula
					normalV.multiply(vLen + 0.06)
					// A rough inverse of vanilla acceleration
					: velocity.multiply(vLen/(vLen+0.06))
				);
			} else {
				cir.setReturnValue(pushForce.multiply(vLen + 0.2));
			}
		}
	}
}