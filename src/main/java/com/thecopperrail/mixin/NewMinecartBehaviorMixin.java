package com.thecopperrail.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.thecopperrail.CopperRailBlock;
import com.thecopperrail.TCRMod;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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

	@Redirect(
		method = "calculateBoostTrackSpeed",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
			ordinal = 0
		),
		require = 1
	)
	private boolean redirectedPoweredRailCheck2(BlockState state, Block block) {
		return state.is(block) || state.is(TCRMod.BLOCK);
	}

	@Redirect(
		method = "calculateBoostTrackSpeed",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;",
			ordinal = 0
		),
		require = 1
	)
	private Vec3 modifyVelocity(
		Vec3 normalizedVelocity,
		double multiplier,
		@Local(argsOnly = true) Vec3 originalVelocity,
		@Local(argsOnly = true) BlockState railState
	) {
		if (!railState.is(TCRMod.BLOCK)) {
			// Minecart is moving along the rail's direction, do not modify the behaviour
			return normalizedVelocity.scale(multiplier);
		}

		Vec3 pushForce = CopperRailBlock.getPushForce(railState);
		// Both pushForce and normalizedVelocity are normal,
		// therefore their dot product is cos(angle between them)
		double correlation = pushForce.dot(normalizedVelocity);
		if (correlation > 0) {
			// Minecart is moving along the rail's direction, do not modify the behaviour
			return normalizedVelocity.scale(multiplier);
		}

		// Original equation: newVelocity = originalVelocity.normalize().scale(originalVelocity.length() + 0.06)
		// Inverse function to use for deceleration:
		// originalVelocity = newVelocity.normalize().scale(newVelocity.length() + 0.06)
		// originalVelocity = newVelocity.scale(1 / newVelocity.length()).scale(newVelocity.length() + 0.06)
		// originalVelocity = newVelocity.scale((newVelocity.length() + 0.06) / newVelocity.length())
		// newVelocity = originalVelocity.scale(newVelocity.length() / (newVelocity.length() + 0.06))

		// Therefore, newVelocity.length() = originalVelocity.length() * newVelocity.length() / (newVelocity.length() + 0.06)
		// which comes up to newVelocity.length() = originalVelocity.length() - 0.06
		double dv = multiplier - originalVelocity.length();
		double newVelocityLength = originalVelocity.length() - dv;

		return originalVelocity.scale(newVelocityLength / (newVelocityLength + dv));
	}
}