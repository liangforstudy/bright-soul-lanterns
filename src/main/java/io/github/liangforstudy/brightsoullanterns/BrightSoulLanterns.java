package io.github.liangforstudy.brightsoullanterns;

import io.github.liangforstudy.brightsoullanterns.mixin.BlockBehaviourAccessor;
import io.github.liangforstudy.brightsoullanterns.mixin.PropertiesAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrightSoulLanterns implements ModInitializer {
	public static final String MOD_ID = "brightsoullanterns";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Identifier SOUL_LANTERN_ID = Identifier.withDefaultNamespace("soul_lantern");
	private static final int FALLBACK_LIGHT = 15;

	@Override
	public void onInitialize() {
		LOGGER.info("Soul lanterns now emit light level {}", Blocks.SOUL_LANTERN.defaultBlockState().getLightEmission());
		ServerChunkEvents.CHUNK_LOAD.register(BrightSoulLanterns::relightSoulLanterns);
	}

	/** Called while blocks are being constructed, before Blocks.SOUL_LANTERN is assigned, so match on the registry id. */
	public static boolean isSoulLantern(Block block) {
		ResourceKey<Block> id = ((PropertiesAccessor) ((BlockBehaviourAccessor) block).brightsoullanterns$getProperties()).brightsoullanterns$getId();
		return id != null && SOUL_LANTERN_ID.equals(id.identifier());
	}

	/** Read the regular lantern's value so the two always match, even if Mojang changes it. */
	public static int regularLanternLight() {
		Block lantern = Blocks.LANTERN; // registered just before the soul lantern
		return lantern != null ? lantern.defaultBlockState().getLightEmission() : FALLBACK_LIGHT;
	}

	/**
	 * Light is saved with the chunk, so soul lanterns placed before the mod was installed
	 * keep their old level-10 light. When a chunk loads, re-run the light check for every
	 * soul lantern in it. Sections whose palette can't contain a soul lantern are skipped,
	 * so chunks without any cost almost nothing. Re-checking a lantern that is already
	 * correct is a no-op for the light engine.
	 */
	private static void relightSoulLanterns(ServerLevel level, LevelChunk chunk, boolean generated) {
		if (generated) {
			return; // fresh chunks are lit with the new value from the start
		}
		LevelChunkSection[] sections = chunk.getSections();
		int baseX = chunk.getPos().getMinBlockX();
		int baseZ = chunk.getPos().getMinBlockZ();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int found = 0;

		for (int i = 0; i < sections.length; i++) {
			LevelChunkSection section = sections[i];
			if (section == null || section.hasOnlyAir() || !section.maybeHas(s -> s.is(Blocks.SOUL_LANTERN))) {
				continue;
			}
			int baseY = chunk.getSectionYFromSectionIndex(i) << 4;
			PalettedContainer<BlockState> states = section.getStates();
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						if (states.get(x, y, z).is(Blocks.SOUL_LANTERN)) {
							pos.set(baseX + x, baseY + y, baseZ + z);
							level.getLightEngine().checkBlock(pos);
							found++;
						}
					}
				}
			}
		}

		if (found > 0) {
			LOGGER.debug("Relit {} soul lantern(s) in chunk {}", found, chunk.getPos());
		}
	}
}
