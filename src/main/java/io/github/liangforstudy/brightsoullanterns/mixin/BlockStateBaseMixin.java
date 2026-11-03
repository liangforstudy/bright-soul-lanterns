package io.github.liangforstudy.brightsoullanterns.mixin;

import io.github.liangforstudy.brightsoullanterns.BrightSoulLanterns;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Block states cache their light emission in a final field when they are built.
 * Overriding the cached value (instead of hooking getLightEmission) means every
 * vanilla system that reads it - the light engine, mob spawning checks, snow/ice
 * melting - sees the same value, with zero per-call overhead.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
	@Shadow @Final @Mutable
	private int lightEmission;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void brightsoullanterns$overrideEmission(Block owner, Property<?>[] propertyKeys, Comparable<?>[] propertyValues, CallbackInfo ci) {
		if (BrightSoulLanterns.isSoulLantern(owner)) {
			this.lightEmission = BrightSoulLanterns.regularLanternLight();
		}
	}
}
